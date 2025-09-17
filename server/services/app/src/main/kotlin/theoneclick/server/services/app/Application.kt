package theoneclick.server.services.app

import io.ktor.server.application.*
import io.ktor.util.logging.*
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.api.coroutines
import theoneclick.server.services.app.dataSources.*
import theoneclick.server.services.app.di.Dependencies
import theoneclick.server.services.app.postgresql.AppDatabase
import theoneclick.server.services.app.repositories.DefaultHomesRepository
import theoneclick.server.services.app.repositories.DefaultUsersRepository
import theoneclick.server.services.app.repositories.HomesRepository
import theoneclick.server.services.app.repositories.UsersRepository
import theoneclick.server.shared.auth.security.DefaultEncryptor
import theoneclick.server.shared.auth.security.DefaultJwtProvider
import theoneclick.server.shared.auth.security.DefaultSecureRandomProvider
import theoneclick.server.shared.auth.security.DefaultUuidProvider
import theoneclick.server.shared.db.databaseDriver
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
        protocol = environment.protocol,
        host = environment.host,
        disableRateLimit = environment.disableRateLimit,
        encryptor = encryptor,
        timeProvider = timeProvider,
        logger = logger,
        jwtProvider = jwtProvider,
        onShutdown = repositories.onShutdown,
        usersRepository = repositories.usersRepository,
        homesRepository = repositories.homesRepository,
        uuidProvider = uuidProvider,
    )

    server(dependencies = dependencies).start(wait = true)
}

private fun memoryRepositories(): Repositories {
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

    return Repositories(
        usersRepository = usersRepository,
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

    return Repositories(
        usersRepository = usersRepository,
        homesRepository = homesRepository,
        onShutdown = {
            databaseDriver.close()
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
    val homesRepository: HomesRepository,
    val onShutdown: (application: Application) -> Unit,
)
