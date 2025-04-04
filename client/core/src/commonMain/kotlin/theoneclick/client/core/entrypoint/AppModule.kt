package theoneclick.client.core.entrypoint

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import theoneclick.client.core.viewModels.InitViewModel
import theoneclick.client.core.viewModels.LoginViewModel

//TODO: Safe module
fun buildAppModule(coreModule: Module): Module =
    module {
        includes(coreModule)

        viewModel {
            InitViewModel(
                navigationController = get(),
                authenticationDataSource = get(),
            )
        }
        viewModel {
            LoginViewModel(
                navigationController = get(),
                authenticationDataSource = get()
            )
        }
    }