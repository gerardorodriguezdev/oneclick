package oneclick.server.services.app

import io.ktor.server.application.*
import io.ktor.util.logging.*
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.api.coroutines
import oneclick.server.services.app.dataSources.*
import oneclick.server.services.app.dataSources.base.InvalidJwtDataSource
import oneclick.server.services.app.di.Dependencies
import oneclick.server.services.app.postgresql.AppDatabase
import oneclick.server.services.app.repositories.DefaultHomesRepository
import oneclick.server.services.app.repositories.DefaultUsersRepository
import oneclick.server.services.app.repositories.HomesRepository
import oneclick.server.services.app.repositories.UsersRepository
import oneclick.server.shared.auth.security.*
import oneclick.server.shared.db.databaseDriver
import oneclick.shared.dispatchers.platform.DispatchersProvider
import oneclick.shared.dispatchers.platform.dispatchersProvider
import oneclick.shared.security.DefaultSecureRandomProvider
import oneclick.shared.timeProvider.SystemTimeProvider
import oneclick.shared.timeProvider.TimeProvider

fun main() {
    val environment = Environment()
    val secureRandomProvider = DefaultSecureRandomProvider()
    val timeProvider = SystemTimeProvider()
    val encryptor = KtorKeystoreEncryptor(
        secretEncryptionKey = environment.secretEncryptionKey,
        secureRandomProvider = secureRandomProvider,
    )
    val passwordManager = BcryptPasswordManager(secureRandomProvider)
    val logger = KtorSimpleLogger("oneclick.defaultlogger")
    val dispatchersProvider = dispatchersProvider()
    val uuidProvider = DefaultUuidProvider()
    val jwtProvider = DefaultJwtProvider(
        jwtRealm = "Oneclick api",
        jwtAudience = environment.jwtAudience,
        jwtIssuer = environment.baseUrl,
        secretSignKey = environment.secretSignKey,
        timeProvider = timeProvider,
        encryptor = encryptor,
        uuidProvider = uuidProvider,
    )
    val repositories = if (environment.useMemoryDataSources) {
        memoryRepositories(
            timeProvider = timeProvider,
            jwtProvider = jwtProvider
        )
    } else {
        databaseRepositories(
            jdbcUrl = environment.jdbcUrl,
            postgresUsername = environment.postgresUsername,
            postgresPassword = environment.postgresPassword,
            redisUrl = environment.redisUrl,
            logger = logger,
            dispatchersProvider = dispatchersProvider,
            jwtProvider = jwtProvider,
        )
    }
    val dependencies = Dependencies(
        protocol = environment.protocol,
        host = environment.host,
        disableRateLimit = environment.disableRateLimit,
        passwordManager = passwordManager,
        timeProvider = timeProvider,
        logger = logger,
        jwtProvider = jwtProvider,
        onShutdown = repositories.onShutdown,
        usersRepository = repositories.usersRepository,
        homesRepository = repositories.homesRepository,
        uuidProvider = uuidProvider,
        invalidJwtDataSource = repositories.invalidJwtDataSource,
    )

    server(dependencies = dependencies).start(wait = true)
}

private fun memoryRepositories(
    timeProvider: TimeProvider,
    jwtProvider: JwtProvider,
): Repositories {
    val memoryUsersDataSource = MemoryUsersDataSource()
    val usersRepository = DefaultUsersRepository(
        diskUsersDataSource = memoryUsersDataSource,
        memoryUsersDataSource = memoryUsersDataSource,
    )

    val memoryHomesDataSource = MemoryHomesDataSource()
    val homesRepository = DefaultHomesRepository(
        memoryHomesDataSource = memoryHomesDataSource,
        diskHomesDataSource = memoryHomesDataSource,
    )

    val memoryInvalidJwtDataSource = MemoryInvalidJwtDataSource(
        jwtExpirationTime = jwtProvider.jwtExpirationTimeInMillis,
        timeProvider = timeProvider,
    )

    return Repositories(
        usersRepository = usersRepository,
        homesRepository = homesRepository,
        invalidJwtDataSource = memoryInvalidJwtDataSource,
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
    jwtProvider: JwtProvider,
): Repositories {
    val databaseDriver = databaseDriver(
        jdbcUrl = jdbcUrl,
        postgresUsername = postgresUsername,
        postgresPassword = postgresPassword,
    )

    val appDatabase = AppDatabase(databaseDriver)
    AppDatabase.Schema.create(databaseDriver)

    val redisClient = RedisClient.create(redisUrl)
    val redisConnection = redisClient.connect()

    val memoryUsersDataSource = RedisUsersDataSource(
        syncCommands = redisConnection.coroutines(),
        dispatchersProvider = dispatchersProvider,
        logger = logger,
    )
    val diskUsersDataSource = PostgresUsersDataSource(appDatabase, dispatchersProvider, logger)
    val usersRepository = DefaultUsersRepository(
        diskUsersDataSource = diskUsersDataSource,
        memoryUsersDataSource = memoryUsersDataSource,
    )

    val memoryHomesDataSource = RedisHomesDataSource(
        syncCommands = redisConnection.coroutines(),
        dispatchersProvider = dispatchersProvider,
        logger = logger,
    )
    val diskHomesDataSource = PostgresHomesDataSource(appDatabase, dispatchersProvider, logger)
    val homesRepository = DefaultHomesRepository(
        memoryHomesDataSource = memoryHomesDataSource,
        diskHomesDataSource = diskHomesDataSource,
    )

    val invalidJwtDataSource = RedisInvalidJwtDataSource(
        syncCommands = redisConnection.coroutines(),
        dispatchersProvider = dispatchersProvider,
        jwtExpirationTime = jwtProvider.jwtExpirationTimeInMillis,
    )

    return Repositories(
        usersRepository = usersRepository,
        homesRepository = homesRepository,
        invalidJwtDataSource = invalidJwtDataSource,
        onShutdown = {
            databaseDriver.close()
            redisConnection.close()
            redisClient.shutdown()
        },
    )
}

private data class Environment(
    val secretEncryptionKey: String = System.getenv("SECRET_ENCRYPTION_KEY"),
    val secretSignKey: String = System.getenv("SECRET_SIGN_KEY"),
    val useMemoryDataSources: Boolean = System.getenv("USE_MEMORY_DATA_SOURCES") == "true",
    val postgresHost: String = System.getenv("POSTGRES_HOST"),
    val postgresDatabase: String = System.getenv("POSTGRES_DATABASE"),
    val postgresUsername: String = System.getenv("POSTGRES_USERNAME"),
    val postgresPassword: String = System.getenv("POSTGRES_PASSWORD"),
    val redisUrl: String = System.getenv("REDIS_URL"),
    val disableRateLimit: Boolean = System.getenv("DISABLE_RATE_LIMIT") == "true",
    val protocol: String = System.getenv("PROTOCOL"),
    val host: String = System.getenv("HOST"),
) {
    val baseUrl: String = "$protocol://$host"
    val jwtAudience: String = "$baseUrl/api"
    val jdbcUrl: String = "jdbc:postgresql://$postgresHost:5432/$postgresDatabase"
}

private class Repositories(
    val usersRepository: UsersRepository,
    val homesRepository: HomesRepository,
    val invalidJwtDataSource: InvalidJwtDataSource,
    val onShutdown: (application: Application) -> Unit,
)
