package theoneclick.client.core.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import theoneclick.client.core.dataSources.LoggedDataSource
import theoneclick.client.core.dataSources.RemoteLoggedDataSource
import theoneclick.client.core.di.base.ModuleProvider
import theoneclick.client.core.extensions.includes
import theoneclick.client.core.repositories.DevicesRepository
import theoneclick.client.core.repositories.InMemoryDevicesRepository
import theoneclick.client.core.viewModels.homeScreen.AddDeviceViewModel
import theoneclick.client.core.viewModels.homeScreen.DevicesListViewModel
import theoneclick.client.core.viewModels.homeScreen.UserSettingsViewModel

class LoggedModule(coreModule: CoreModule) : ModuleProvider {
    override val module: Module =
        module {
            includes(coreModule)

            singleOf(::LoggedScope)

            scope<LoggedScope> {
                scopedOf(::RemoteLoggedDataSource) bind LoggedDataSource::class
                scopedOf(::InMemoryDevicesRepository) bind DevicesRepository::class
            }

            viewModel {
                val loggedScope: LoggedScope = get()
                DevicesListViewModel(
                    devicesRepository = loggedScope.scope.get(),
                )
            }

            viewModel {
                val loggedScope: LoggedScope = get()
                AddDeviceViewModel(
                    devicesRepository = loggedScope.scope.get(),
                )
            }

            viewModel {
                val loggedScope: LoggedScope = get()
                UserSettingsViewModel(
                    authenticationDataSource = loggedScope.scope.get(),
                    navigationController = get(),
                )
            }
        }
}
