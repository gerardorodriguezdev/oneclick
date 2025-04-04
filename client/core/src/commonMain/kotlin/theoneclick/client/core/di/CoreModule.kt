package theoneclick.client.core.di

import org.koin.dsl.module
import theoneclick.client.core.di.base.ModuleProvider
import theoneclick.client.core.platform.AppDependencies

class CoreModule(appDependencies: AppDependencies) : ModuleProvider {
    override val module = module {
        single { appDependencies.navigationController }
        single { appDependencies.dispatchersProvider }
        single { appDependencies.httpClient }
        single { appDependencies.authenticationDataSource }
        single { appDependencies.appLogger }
    }
}
