package theoneclick.client.app.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import theoneclick.client.app.dataSources.LoggedDataSource
import theoneclick.client.app.dataSources.RemoteLoggedDataSource
import theoneclick.client.app.di.base.ModuleProvider
import theoneclick.client.app.extensions.includes
import theoneclick.client.app.platform.AuthenticationDataSource
import theoneclick.client.app.repositories.DevicesRepository
import theoneclick.client.app.repositories.InMemoryDevicesRepository
import theoneclick.client.app.viewModels.homeScreen.AddDeviceViewModel
import theoneclick.client.app.viewModels.homeScreen.DevicesListViewModel
import theoneclick.client.app.viewModels.homeScreen.UserSettingsViewModel

class HomeModule(coreModule: CoreModule) : ModuleProvider {
    override val module: Module =
        module {
            includes(coreModule)

            scope(named(HOME_SCOPE)) {
                scopedOf(::RemoteLoggedDataSource) bind LoggedDataSource::class
                scopedOf(::InMemoryDevicesRepository) bind DevicesRepository::class

                viewModel {
                    DevicesListViewModel(devicesRepository = get<DevicesRepository>())
                }

                viewModel {
                    AddDeviceViewModel(devicesRepository = get<DevicesRepository>())
                }

                viewModel {
                    UserSettingsViewModel(authenticationDataSource = get<AuthenticationDataSource>())
                }
            }
        }

    companion object {
        const val HOME_SCOPE = "HOME_SCOPE"
    }
}
