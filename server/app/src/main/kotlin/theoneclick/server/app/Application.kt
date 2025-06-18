package theoneclick.server.app

import io.ktor.util.logging.*
import theoneclick.server.app.dataSources.FileSystemUsersDataSource
import theoneclick.server.app.dataSources.InMemoryUsersDataSource
import theoneclick.server.app.di.AppComponent
import theoneclick.server.app.di.Environment
import theoneclick.server.app.di.create
import theoneclick.server.app.entrypoint.server
import theoneclick.server.app.repositories.DefaultUsersRepository
import theoneclick.server.app.security.DefaultEncryptor
import theoneclick.server.app.security.DefaultIvGenerator
import theoneclick.server.app.security.DefaultSecureRandomProvider
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
    val diskUsersDataSource = FileSystemUsersDataSource(
        usersDirectory = FileSystemUsersDataSource.usersDirectory(environment.storageDirectory),
        encryptor = encryptor,
        logger = logger,
    )
    val inMemoryUsersDataSource = InMemoryUsersDataSource()
    val usersRepository = DefaultUsersRepository(
        diskUsersDataSource = diskUsersDataSource,
        memoryUsersDataSource = inMemoryUsersDataSource,
    )
    val appComponent = AppComponent::class.create(
        environment = environment,
        ivGenerator = ivGenerator,
        encryptor = encryptor,
        timeProvider = timeProvider,
        logger = logger,
        usersRepository = usersRepository,
    )
    server(appComponent).start(wait = true)
}