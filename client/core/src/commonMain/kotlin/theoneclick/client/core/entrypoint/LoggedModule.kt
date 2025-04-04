package theoneclick.client.core.entrypoint

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import theoneclick.client.core.dataSources.LoggedDataSource
import theoneclick.client.core.dataSources.RemoteLoggedDataSource
import theoneclick.client.core.repositories.DevicesRepository
import theoneclick.client.core.repositories.InMemoryDevicesRepository
import theoneclick.client.core.viewModels.homeScreen.AddDeviceViewModel
import theoneclick.client.core.viewModels.homeScreen.DevicesListViewModel
import theoneclick.client.core.viewModels.homeScreen.UserSettingsViewModel

//TODO: Safe module
fun buildLoggedModule(coreModule: Module): Module =
    module {
        includes(coreModule)

        //TODO: Fix scopes
        singleOf(::RemoteLoggedDataSource) bind LoggedDataSource::class

        singleOf(::InMemoryDevicesRepository) bind DevicesRepository::class

        viewModel {
            DevicesListViewModel(
                devicesRepository = get(),
            )
        }

        viewModel {
            AddDeviceViewModel(
                devicesRepository = get(),
            )
        }

        viewModel {
            UserSettingsViewModel(
                authenticationDataSource = get(),
                navigationController = get(),
            )
        }
    }