package theoneclick.server.services.auth

import io.ktor.server.application.*
import io.ktor.util.logging.*
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.api.coroutines
import theoneclick.server.services.auth.dataSources.MemoryUsersDataSource
import theoneclick.server.services.auth.dataSources.PostgresUsersDataSource
import theoneclick.server.services.auth.dataSources.RedisUsersDataSource
import theoneclick.server.services.auth.plugins.configureRouting
import theoneclick.server.services.auth.postgresql.AuthDatabase
import theoneclick.server.services.auth.repositories.DefaultUsersRepository
import theoneclick.server.services.auth.repositories.UsersRepository
import theoneclick.server.shared.auth.security.*
import theoneclick.server.shared.core.di.Dependencies
import theoneclick.server.shared.core.extensions.databaseDriver
import theoneclick.server.shared.core.server
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.dispatchers.platform.dispatchersProvider
import theoneclick.shared.timeProvider.SystemTimeProvider

fun main() {
    val environment = Environment()
    val jvmSecureRandomProvider = DefaultSecureRandomProvider()
    val timeProvider = SystemTimeProvider()
    val encryptor = DefaultEncryptor(
        secretEncryptionKey = environment.secretEncryptionKey,
        secureRandomProvider = jvmSecureRandomProvider,
    )
    val logger = KtorSimpleLogger("theoneclick.defaultlogger")
    val dispatchersProvider = dispatchersProvider()
    val repositories = if (environment.useMemoryDataSources) {
        memoryRepositories()
    } else {
        databaseRepositories(
            jdbcUrl = environment.jdbcUrl,
            postgresUsername = environment.postgresUsername,
            postgresPassword = environment.postgresPassword,
            redisUrl = environment.redisUrl,
            logger = logger,
            dispatchersProvider = dispatchersProvider,
        )
    }
    val jwtProvider = DefaultJwtProvider(
        jwtRealm = environment.jwtRealm,
        jwtAudience = environment.jwtAudience,
        jwtIssuer = environment.jwtIssuer,
        secretSignKey = environment.secretSignKey,
        timeProvider = timeProvider,
        encryptor = encryptor,
    )
    val uuidProvider = DefaultUuidProvider()
    val dependencies = Dependencies(
        disableRateLimit = environment.disableRateLimit,
        healthzPath = "/api/healthz/auth",
        encryptor = encryptor,
        timeProvider = timeProvider,
        logger = logger,
        jwtProvider = jwtProvider,
        baseUrl = "${environment.protocol}://${environment.host}",
    )

    server(
        dependencies = dependencies,
        uuidProvider = uuidProvider,
        usersRepository = repositories.usersRepository,
        onShutdown = repositories.onShutdown
    )
}

internal fun server(
    dependencies: Dependencies,
    uuidProvider: UuidProvider,
    usersRepository: UsersRepository,
    onShutdown: (application: Application) -> Unit,
) {
    server(
        dependencies = dependencies,
        configureModules = {
            configureRouting(
                usersRepository = usersRepository,
                encryptor = dependencies.encryptor,
                uuidProvider = uuidProvider,
                jwtProvider = dependencies.jwtProvider,
            )
        },
        onShutdown = onShutdown,
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

private data class Environment(
    val secretEncryptionKey: String = System.getenv("JWT_SECRET_ENCRYPTION_KEY"),
    val secretSignKey: String = System.getenv("JWT_SECRET_SIGN_KEY"),
    val useMemoryDataSources: Boolean = System.getenv("USE_MEMORY_DATA_SOURCES") == "true",
    val jdbcUrl: String = System.getenv("JDBC_URL"),
    val postgresUsername: String = System.getenv("POSTGRES_USERNAME"),
    val postgresPassword: String = System.getenv("POSTGRES_PASSWORD"),
    val redisUrl: String = System.getenv("REDIS_URL"),
    val jwtRealm: String = System.getenv("JWT_REALM"),
    val jwtAudience: String = System.getenv("JWT_AUDIENCE"),
    val jwtIssuer: String = System.getenv("JWT_ISSUER"),
    val disableRateLimit: Boolean = System.getenv("DISABLE_RATE_LIMIT") == "true",
    val protocol: String = System.getenv("PROTOCOL"),
    val host: String = System.getenv("HOST"),
)

private class Repositories(
    val usersRepository: UsersRepository,
    val onShutdown: (application: Application) -> Unit,
)
