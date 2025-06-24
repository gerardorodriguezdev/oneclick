package theoneclick.server.app

import io.ktor.util.logging.*
import theoneclick.server.shared.dataSources.DiskHomesDataSource
import theoneclick.server.shared.dataSources.DiskUsersDataSource
import theoneclick.server.shared.dataSources.MemoryHomesDataSource
import theoneclick.server.shared.dataSources.MemoryUsersDataSource
import theoneclick.server.app.di.AppComponent
import theoneclick.server.shared.di.Environment
import theoneclick.server.app.di.create
import theoneclick.server.app.entrypoint.server
import theoneclick.server.shared.repositories.DefaultHomesRepository
import theoneclick.server.shared.repositories.DefaultUsersRepository
import theoneclick.server.shared.security.DefaultEncryptor
import theoneclick.server.shared.security.DefaultIvGenerator
import theoneclick.server.shared.security.DefaultSecureRandomProvider
import theoneclick.shared.timeProvider.SystemTimeProvider

fun main() {
    val environment = Environment(
        secretSignKey = System.getenv("SECRET_SIGN_KEY"),
        secretEncryptionKey = System.getenv("SECRET_ENCRYPTION_KEY"),
        protocol = System.getenv("PROTOCOL"),
        host = System.getenv("HOST"),
        storageDirectory = System.getenv("STORAGE_DIRECTORY"),
        enableQAAPI = System.getenv("ENABLE_QAAPI") == "true",
        disableRateLimit = System.getenv("DISABLE_RATE_LIMIT") == "true",
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
    val diskUsersDataSource = DiskUsersDataSource(
        usersDirectory = DiskUsersDataSource.usersDirectory(environment.storageDirectory),
        encryptor = encryptor,
        logger = logger,
    )
    val memoryUsersDataSource = MemoryUsersDataSource()
    val usersRepository = DefaultUsersRepository(
        diskUsersDataSource = diskUsersDataSource,
        memoryUsersDataSource = memoryUsersDataSource,
    )
    val diskHomesDataSource = DiskHomesDataSource(
        homesEntriesDirectory = DiskHomesDataSource.homesEntriesDirectory(environment.storageDirectory),
        encryptor = encryptor,
        logger = logger,
    )
    val memoryHomesDataSource = MemoryHomesDataSource()
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
        homesRepository = homesRepository,
    )
    server(appComponent).start(wait = true)
}