package theoneclick.server.app

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.util.logging.*
import theoneclick.server.app.di.AppComponent
import theoneclick.server.app.di.create
import theoneclick.server.app.entrypoint.server
import theoneclick.server.shared.dataSources.*
import theoneclick.server.shared.di.Environment
import theoneclick.server.shared.postgresql.UsersDatabase
import theoneclick.server.shared.repositories.DefaultHomesRepository
import theoneclick.server.shared.repositories.DefaultSessionsRepository
import theoneclick.server.shared.repositories.DefaultUsersRepository
import theoneclick.server.shared.security.DefaultEncryptor
import theoneclick.server.shared.security.DefaultIvGenerator
import theoneclick.server.shared.security.DefaultSecureRandomProvider
import theoneclick.shared.dispatchers.platform.dispatchersProvider
import theoneclick.shared.timeProvider.SystemTimeProvider

fun main() {
    val environment = Environment(
        secretSignKey = System.getenv("SECRET_SIGN_KEY"),
        secretEncryptionKey = System.getenv("SECRET_ENCRYPTION_KEY"),
        protocol = System.getenv("PROTOCOL"),
        host = System.getenv("HOST"),
        jdbcUrl = System.getenv("JDBC_URL"),
        dbUsername = System.getenv("DB_USERNAME"),
        dbPassword = System.getenv("DB_PASSWORD"),
        enableQAAPI = System.getenv("ENABLE_QAAPI") == "true",
        disableRateLimit = System.getenv("DISABLE_RATE_LIMIT") == "true",
        useMemoryDatabases = System.getenv("USE_MEMORY_DATABASES") == "true",
    )

    val jvmSecureRandomProvider = DefaultSecureRandomProvider()
    val timeProvider = SystemTimeProvider()
    val encryptor = DefaultEncryptor(
        secretEncryptionKey = environment.secretEncryptionKey,
        secureRandomProvider = jvmSecureRandomProvider,
        timeProvider = timeProvider,
    )
    val ivGenerator = DefaultIvGenerator(jvmSecureRandomProvider)
    val logger = KtorSimpleLogger("theoneclick.defaultlogger")
    val dispatchersProvider = dispatchersProvider()

    val hikariConfig = HikariConfig().apply {
        jdbcUrl = environment.jdbcUrl
        username = environment.dbUsername
        password = environment.dbPassword
        validate()
    }
    val hikariDataSource = HikariDataSource(hikariConfig)
    val driver = hikariDataSource.asJdbcDriver()
    val usersDatabase = UsersDatabase(driver)

    val memoryUsersDataSource = MemoryUsersDataSource()
    val diskUsersDataSource = if (environment.useMemoryDatabases) {
        memoryUsersDataSource
    } else {
        PostgresUsersDataSource(database = usersDatabase, dispatchersProvider = dispatchersProvider, logger = logger)
    }
    val usersRepository = DefaultUsersRepository(
        diskUsersDataSource = diskUsersDataSource,
        memoryUsersDataSource = memoryUsersDataSource,
    )

    val memorySessionsDataSource = MemorySessionsDataSource()
    val diskSessionsDataSource = if (environment.useMemoryDatabases) {
        memorySessionsDataSource
    } else {
        //TODO: Finish
        memorySessionsDataSource
    }
    val sessionsRepository = DefaultSessionsRepository(
        memorySessionsDataSource = memorySessionsDataSource,
        diskSessionsDataSource = diskSessionsDataSource,
    )

    val memoryHomesDataSource = MemoryHomesDataSource()
    val diskHomesDataSource = if (environment.useMemoryDatabases) {
        memoryHomesDataSource
    } else {
        PostgresHomesDataSource(
            database = usersDatabase,
            dispatchersProvider = dispatchersProvider,
            logger = logger,
        )
    }
    val homesRepository = DefaultHomesRepository(
        memoryHomesDataSource = memoryHomesDataSource,
        diskHomesDataSource = diskHomesDataSource,
    )

    val appComponent = AppComponent::class.create(
        environment = environment,
        ivGenerator = ivGenerator,
        encryptor = encryptor,
        timeProvider = timeProvider,
        logger = logger,
        usersRepository = usersRepository,
        sessionsRepository = sessionsRepository,
        homesRepository = homesRepository,
    )
    server(appComponent).start(wait = true)
}