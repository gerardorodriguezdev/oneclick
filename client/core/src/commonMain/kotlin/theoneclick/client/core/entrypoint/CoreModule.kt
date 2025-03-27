package theoneclick.client.core.entrypoint

import org.koin.core.module.Module
import org.koin.dsl.module
import theoneclick.client.core.platform.AppDependencies

fun buildCoreModule(coreDependencies: AppDependencies): Module =
    module {
        single { coreDependencies.navigationController }
        single { coreDependencies.dispatchersProvider }
        single { coreDependencies.httpClient }
        single { coreDependencies.authenticationDataSource }
        single { coreDependencies.appLogger }
    }
