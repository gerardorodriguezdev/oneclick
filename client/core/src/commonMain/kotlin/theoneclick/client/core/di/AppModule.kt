package theoneclick.client.core.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import theoneclick.client.core.di.base.ModuleProvider
import theoneclick.client.core.extensions.includes
import theoneclick.client.core.viewModels.InitViewModel
import theoneclick.client.core.viewModels.LoginViewModel

class AppModule(coreModule: CoreModule) : ModuleProvider {
    override val module = module {
        includes(coreModule)

        viewModelOf(::InitViewModel)
        viewModelOf(::LoginViewModel)
    }
}