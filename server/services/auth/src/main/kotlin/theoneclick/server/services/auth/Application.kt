package theoneclick.server.services.auth

import io.ktor.server.application.*
import io.ktor.util.logging.*
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.api.coroutines
import theoneclick.server.services.auth.dataSources.MemoryUsersDataSource
import theoneclick.server.services.auth.dataSources.PostgresUsersDataSource
import theoneclick.server.services.auth.dataSources.RedisUsersDataSource
import theoneclick.server.services.auth.di.AppComponent
import theoneclick.server.services.auth.plugins.configureRouting
import theoneclick.server.services.auth.postgresql.AuthDatabase
import theoneclick.server.services.auth.repositories.DefaultUsersRepository
import theoneclick.server.services.auth.repositories.UsersRepository
import theoneclick.server.shared.auth.security.DefaultEncryptor
import theoneclick.server.shared.auth.security.DefaultJwtProvider
import theoneclick.server.shared.auth.security.DefaultSecureRandomProvider
import theoneclick.server.shared.auth.security.DefaultUuidProvider
import theoneclick.server.shared.core.extensions.databaseDriver
import theoneclick.server.shared.core.server
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.dispatchers.platform.dispatchersProvider
import theoneclick.shared.timeProvider.SystemTimeProvider

fun main() {
    val jvmSecureRandomProvider = DefaultSecureRandomProvider()
    val timeProvider = SystemTimeProvider()
    val encryptor = DefaultEncryptor(
        secretEncryptionKey = System.getenv("JWT_SECRET_ENCRYPTION_KEY"),
        secureRandomProvider = jvmSecureRandomProvider,
    )
    val logger = KtorSimpleLogger("theoneclick.defaultlogger")
    val dispatchersProvider = dispatchersProvider()
    val repositories = if (System.getenv("USE_MEMORY_DATA_SOURCES") == "true") {
        memoryRepositories()
    } else {
        databaseRepositories(
            jdbcUrl = System.getenv("JDBC_URL"),
            postgresUsername = System.getenv("POSTGRES_USERNAME"),
            postgresPassword = System.getenv("POSTGRES_PASSWORD"),
            redisUrl = System.getenv("REDIS_URL"),
            logger = logger,
            dispatchersProvider = dispatchersProvider,
        )
    }

    val jwtProvider = DefaultJwtProvider(
        jwtRealm = System.getenv("JWT_REALM"),
        jwtAudience = System.getenv("JWT_AUDIENCE"),
        jwtIssuer = System.getenv("JWT_ISSUER"),
        secretSignKey = System.getenv("JWT_SECRET_SIGN_KEY"),
        timeProvider = timeProvider,
        encryptor = encryptor,
    )
    val uuidProvider = DefaultUuidProvider()

    val appComponent = AppComponent(
        disableRateLimit = System.getenv("DISABLE_RATE_LIMIT") == "true",
        protocol = System.getenv("PROTOCOL"),
        host = System.getenv("HOST"),
        healthzPath = "/api/healthz/auth",
        encryptor = encryptor,
        timeProvider = timeProvider,
        logger = logger,
        jwtProvider = jwtProvider,
    )
    server(
        dependencies = appComponent,
        configureModules = {
            configureRouting(
                usersRepository = repositories.usersRepository,
                encryptor = appComponent.encryptor,
                uuidProvider = uuidProvider,
                jwtProvider = appComponent.jwtProvider,
            )
        },
        onShutdown = { application ->
            repositories.onShutdown(application)
        }
    ).start(wait = true)
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
    jdbcUrl: String,
    postgresUsername: String,
    postgresPassword: String,
    redisUrl: String,
    logger: Logger,
    dispatchersProvider: DispatchersProvider,
): Repositories {
    val driver = databaseDriver(
        jdbcUrl = jdbcUrl,
        postgresUsername = postgresUsername,
        postgresPassword = postgresPassword,
    )

    val authDatabase = AuthDatabase(driver)

    val redisClient = RedisClient.create(redisUrl)
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
