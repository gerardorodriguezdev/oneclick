package theoneclick.server.app.platform.base

import io.ktor.util.logging.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import theoneclick.server.app.dataSources.FileSystemUsersDataSource
import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.models.Path
import theoneclick.server.app.platform.*
import theoneclick.server.app.validators.ParamsValidator
import theoneclick.shared.timeProvider.SystemTimeProvider
import theoneclick.shared.timeProvider.TimeProvider

interface Dependencies {
    val environment: Environment
    val timeProvider: TimeProvider
    val uuidProvider: UuidProvider
    val ivGenerator: IvGenerator
    val securityUtils: SecurityUtils
    val pathProvider: PathProvider
    val fileSystem: FileSystem
}

class JvmDependencies(
    override val environment: Environment,
    val directory: Path,
) : Dependencies {
    private val jvmSecureRandomProvider = DefaultJvmSecureRandomProvider()

    override val timeProvider: TimeProvider = SystemTimeProvider()
    override val uuidProvider: UuidProvider = DefaultUuidProvider()
    override val ivGenerator: IvGenerator = JvmIvGenerator(jvmSecureRandomProvider)
    override val securityUtils: SecurityUtils = JvmSecurityUtils(
        secretEncryptionKey = environment.secretEncryptionKey,
        jvmSecureRandomProvider = jvmSecureRandomProvider,
        timeProvider = timeProvider,
    )
    override val fileSystem: FileSystem = JvmFileSystem()
    override val pathProvider: PathProvider = PathProvider(
        directory = directory,
        fileSystem = fileSystem,
    )
}

fun buildModule(dependencies: Dependencies): Module =
    module {
        single<TimeProvider> { dependencies.timeProvider }
        single<UuidProvider> { dependencies.uuidProvider }
        single<IvGenerator> { dependencies.ivGenerator }
        single<SecurityUtils> { dependencies.securityUtils }
        single<Environment> { dependencies.environment }
        single<FileSystem> { dependencies.fileSystem }
        single<PathProvider> { dependencies.pathProvider }
        single<Logger> { KtorSimpleLogger("theoneclick.defaultlogger") }
        singleOf(::FileSystemUsersDataSource) bind UsersDataSource::class
        singleOf(::ParamsValidator)
    }
