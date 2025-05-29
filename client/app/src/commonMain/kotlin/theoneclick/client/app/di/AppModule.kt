package theoneclick.client.app.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import theoneclick.client.app.di.base.ModuleProvider
import theoneclick.client.app.extensions.includes
import theoneclick.client.app.viewModels.InitViewModel
import theoneclick.client.app.viewModels.LoginViewModel

class AppModule(coreModule: CoreModule) : ModuleProvider {
    override val module = module {
        includes(coreModule)

        viewModelOf(::InitViewModel)
        viewModelOf(::LoginViewModel)
    }
}
