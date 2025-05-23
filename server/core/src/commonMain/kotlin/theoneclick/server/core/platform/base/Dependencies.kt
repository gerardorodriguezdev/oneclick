package theoneclick.server.core.platform.base

import io.ktor.util.logging.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import theoneclick.server.core.dataSources.FileSystemUsersDataSource
import theoneclick.server.core.dataSources.UsersDataSource
import theoneclick.server.core.platform.*
import theoneclick.server.core.validators.ParamsValidator
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
