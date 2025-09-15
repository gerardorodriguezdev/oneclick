package theoneclick.server.services.homes

import io.ktor.server.application.*
import io.ktor.util.logging.*
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.api.coroutines
import theoneclick.server.services.homes.dataSources.MemoryHomesDataSource
import theoneclick.server.services.homes.dataSources.PostgresHomesDataSource
import theoneclick.server.services.homes.dataSources.RedisHomesDataSource
import theoneclick.server.services.homes.plugins.configureRouting
import theoneclick.server.services.homes.postgresql.HomesDatabase
import theoneclick.server.services.homes.repositories.DefaultHomesRepository
import theoneclick.server.services.homes.repositories.HomesRepository
import theoneclick.server.shared.auth.security.DefaultEncryptor
import theoneclick.server.shared.auth.security.DefaultJwtProvider
import theoneclick.server.shared.auth.security.DefaultSecureRandomProvider
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
    val dependencies = Dependencies(
        disableRateLimit = environment.disableRateLimit,
        healthzPath = "/api/healthz/homes",
        encryptor = encryptor,
        timeProvider = timeProvider,
        logger = logger,
        jwtProvider = jwtProvider,
        baseUrl = "${environment.protocol}://${environment.host}",
    )
    server(
        dependencies = dependencies,
        homesRepository = repositories.homesRepository,
        onShutdown = repositories.onShutdown
    )
}

internal fun server(
    dependencies: Dependencies,
    homesRepository: HomesRepository,
    onShutdown: (application: Application) -> Unit,
) {
    server(
        dependencies = dependencies,
        configureModules = {
            configureRouting(
                homesRepository = homesRepository,
            )
        },
        onShutdown = onShutdown,
    ).start(wait = true)
}

private fun memoryRepositories(): Repositories {
    val memoryHomesDataSource = MemoryHomesDataSource()
    val homesRepository = DefaultHomesRepository(
        memoryHomesDataSource = memoryHomesDataSource,
        diskHomesDataSource = memoryHomesDataSource,
    )

    return Repositories(
        homesRepository = homesRepository,
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
    val homesDatabase = HomesDatabase(driver)

    val redisClient = RedisClient.create(redisUrl)
    val redisConnection = redisClient.connect()

    val memoryHomesDataSource = RedisHomesDataSource(
        syncCommands = redisConnection.coroutines(),
        dispatchersProvider = dispatchersProvider,
        logger = logger,
    )
    val diskHomesDataSource = PostgresHomesDataSource(homesDatabase, dispatchersProvider, logger)
    val homesRepository = DefaultHomesRepository(
        memoryHomesDataSource = memoryHomesDataSource,
        diskHomesDataSource = diskHomesDataSource,
    )

    return Repositories(
        homesRepository = homesRepository,
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
    val homesRepository: HomesRepository,
    val onShutdown: (application: Application) -> Unit,
)
