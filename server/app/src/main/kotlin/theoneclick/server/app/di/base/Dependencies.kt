package theoneclick.server.app.di.base

import io.ktor.util.logging.*
import org.koin.core.module.Module
import org.koin.dsl.module
import theoneclick.server.app.dataSources.AuthenticationDataSource
import theoneclick.server.app.dataSources.DefaultAuthenticationDataSource
import theoneclick.server.app.dataSources.FileSystemUsersDataSource
import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.di.Environment
import theoneclick.server.app.security.*
import theoneclick.shared.timeProvider.SystemTimeProvider
import theoneclick.shared.timeProvider.TimeProvider

interface Dependencies {
    val environment: Environment
    val timeProvider: TimeProvider
    val uuidProvider: UuidProvider
    val ivGenerator: IvGenerator
    val encryptor: Encryptor
}

class JvmDependencies(
    override val environment: Environment,
) : Dependencies {
    private val jvmSecureRandomProvider = DefaultSecureRandomProvider()
    override val timeProvider: TimeProvider = SystemTimeProvider()
    override val uuidProvider: UuidProvider = DefaultUuidProvider()
    override val ivGenerator: IvGenerator = DefaultIvGenerator(jvmSecureRandomProvider)
    override val encryptor: Encryptor = DefaultEncryptor(
        secretEncryptionKey = environment.secretEncryptionKey,
        secureRandomProvider = jvmSecureRandomProvider,
        timeProvider = timeProvider,
    )
}

fun buildModule(dependencies: Dependencies): Module =
    module {
        single<TimeProvider> { dependencies.timeProvider }
        single<UuidProvider> { dependencies.uuidProvider }
        single<IvGenerator> { dependencies.ivGenerator }
        single<Encryptor> { dependencies.encryptor }
        single<Environment> { dependencies.environment }
        single<Logger> { KtorSimpleLogger("theoneclick.defaultlogger") }
        single<UsersDataSource> {
            val environment: Environment = get()
            FileSystemUsersDataSource(
                storageDirectory = get<Environment>().storageDirectory,
                encryptor = get(),
                logger = get(),
            )
        }
        single<AuthenticationDataSource> {
            DefaultAuthenticationDataSource(usersDataSource = get(), timeProvider = get())
        }
    }
