package theoneclick.server.services.auth

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.util.logging.*
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.api.coroutines
import theoneclick.server.services.auth.dataSources.MemoryUsersDataSource
import theoneclick.server.services.auth.dataSources.PostgresUsersDataSource
import theoneclick.server.services.auth.dataSources.RedisUsersDataSource
import theoneclick.server.services.auth.di.AppComponent
import theoneclick.server.services.auth.di.create
import theoneclick.server.services.auth.entrypoint.server
import theoneclick.server.services.auth.postgresql.AuthDatabase
import theoneclick.server.services.auth.repositories.DefaultUsersRepository
import theoneclick.server.services.auth.repositories.UsersRepository
import theoneclick.server.shared.di.Environment
import theoneclick.server.shared.security.DefaultEncryptor
import theoneclick.server.shared.security.DefaultSecureRandomProvider
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.dispatchers.platform.dispatchersProvider
import theoneclick.shared.timeProvider.SystemTimeProvider

fun main() {
    val environment = Environment(
        jwtSignKey = System.getenv("JWT_SECRET_SIGN_KEY"),
        jwtEncryptionKey = System.getenv("JWT_SECRET_ENCRYPTION_KEY"),
        jwtRealm = System.getenv("JWT_REALM"),
        jwtAudience = System.getenv("JWT_AUDIENCE"),
        jwtIssuer = System.getenv("JWT_ISSUER"),
        protocol = System.getenv("PROTOCOL"),
        host = System.getenv("HOST"),
        jdbcUrl = System.getenv("JDBC_URL"),
        postgresUsername = System.getenv("POSTGRES_USERNAME"),
        postgresPassword = System.getenv("POSTGRES_PASSWORD"),
        redisUrl = System.getenv("REDIS_URL"),
        redisUsername = System.getenv("REDIS_USERNAME"),
        redisPassword = System.getenv("REDIS_PASSWORD"),
        enableQAAPI = System.getenv("ENABLE_QAAPI") == "true",
        disableRateLimit = System.getenv("DISABLE_RATE_LIMIT") == "true",
        useMemoryDatabases = System.getenv("USE_MEMORY_DATA_SOURCES") == "true",
    )

    val jvmSecureRandomProvider = DefaultSecureRandomProvider()
    val timeProvider = SystemTimeProvider()
    val encryptor = DefaultEncryptor(
        jwtIssuer = environment.jwtIssuer,
        jwtAudience = environment.jwtAudience,
        secretSignKey = environment.jwtSignKey,
        secretEncryptionKey = environment.jwtEncryptionKey,
        secureRandomProvider = jvmSecureRandomProvider,
        timeProvider = timeProvider,
    )
    val logger = KtorSimpleLogger("theoneclick.defaultlogger")
    val dispatchersProvider = dispatchersProvider()
    val repositories = if (environment.useMemoryDatabases) {
        memoryRepositories()
    } else {
        databaseRepositories(environment, logger, dispatchersProvider)
    }

    val appComponent = AppComponent::class.create(
        environment = environment,
        encryptor = encryptor,
        timeProvider = timeProvider,
        logger = logger,
        usersRepository = repositories.usersRepository,
        onShutdown = { application ->
            repositories.onShutdown(application)
        }
    )
    server(appComponent).start(wait = true)
}

private fun memoryRepositories(): Repositories {
    val memoryUsersDataSource = MemoryUsersDataSource()
    val usersRepository = DefaultUsersRepository(
        diskUsersDataSource = memoryUsersDataSource,
        memoryUsersDataSource = memoryUsersDataSource,
    )

    return Repositories(
        usersRepository = usersRepository,
        onShutdown = {},
    )
}

@OptIn(ExperimentalLettuceCoroutinesApi::class)
private fun databaseRepositories(
    environment: Environment,
    logger: Logger,
    dispatchersProvider: DispatchersProvider,
): Repositories {
    val hikariConfig = HikariConfig().apply {
        jdbcUrl = environment.jdbcUrl
        username = environment.postgresUsername
        password = environment.postgresPassword
        validate()
    }
    val hikariDataSource = HikariDataSource(hikariConfig)
    val driver = hikariDataSource.asJdbcDriver()
    val authDatabase = AuthDatabase(driver)
    AuthDatabase.Schema.create(driver)

    val redisClient = RedisClient.create(environment.redisUrl)
    val redisConnection = redisClient.connect()

    val memoryUsersDataSource = RedisUsersDataSource(
        syncCommands = redisConnection.coroutines(),
        dispatchersProvider = dispatchersProvider,
        logger = logger,
    )
    val diskUsersDataSource = PostgresUsersDataSource(authDatabase, dispatchersProvider, logger)
    val usersRepository = DefaultUsersRepository(
        diskUsersDataSource = diskUsersDataSource,
        memoryUsersDataSource = memoryUsersDataSource,
    )

    return Repositories(
        usersRepository = usersRepository,
        onShutdown = {
            driver.close()
            redisConnection.close()
            redisClient.shutdown()
        },
    )
}

private class Repositories(
    val usersRepository: UsersRepository,
    val onShutdown: (application: Application) -> Unit,
)
