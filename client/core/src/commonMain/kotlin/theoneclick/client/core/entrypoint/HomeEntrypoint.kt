package theoneclick.client.core.entrypoint

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import theoneclick.client.core.dataSources.LoggedDataSource
import theoneclick.client.core.dataSources.RemoteLoggedDataSource
import theoneclick.client.core.repositories.DevicesRepository
import theoneclick.client.core.repositories.InMemoryDevicesRepository
import theoneclick.client.core.ui.screens.homeScreen.AddDeviceScreen
import theoneclick.client.core.ui.screens.homeScreen.DevicesListScreen
import theoneclick.client.core.ui.screens.homeScreen.HomeScreenScaffold
import theoneclick.client.core.ui.screens.homeScreen.UserSettingsScreen
import theoneclick.client.core.viewModels.homeScreen.AddDeviceViewModel
import theoneclick.client.core.viewModels.homeScreen.DevicesListViewModel
import theoneclick.client.core.viewModels.homeScreen.UserSettingsViewModel
import theoneclick.shared.core.models.routes.HomeRoute
import theoneclick.shared.core.models.routes.HomeRoute.*

class HomeEntrypoint {

    // Only visible for testing
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

    @Composable
    fun HomeScreen(navHostController: NavHostController = rememberNavController()) {
        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val selectedHomeRoute = currentDestination.toHomeRoute()

        HomeScreenScaffold(
            selectedHomeRoute = selectedHomeRoute,
            onHomeRouteClick = { homeRoute -> handleHomeRouteClick(navHostController, homeRoute) },
            content = {
                NavHost(
                    navController = navHostController,
                    startDestination = DevicesList,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    composable<DevicesList> {
                        val devicesListViewModel: DevicesListViewModel = koinViewModel()
                        DevicesListScreen(
                            state = devicesListViewModel.state.value,
                            onEvent = devicesListViewModel::onEvent,
                        )
                    }

                    composable<AddDevice> {
                        val addDeviceViewModel: AddDeviceViewModel = koinViewModel()
                        AddDeviceScreen(
                            state = addDeviceViewModel.state.value,
                            onEvent = addDeviceViewModel::onEvent,
                        )
                    }

                    composable<UserSettings> {
                        val userSettingsViewModel: UserSettingsViewModel = koinViewModel()

                        UserSettingsScreen(
                            state = userSettingsViewModel.state.value,
                            onEvent = userSettingsViewModel::onEvent,
                        )
                    }
                }
            }
        )
    }

    private fun handleHomeRouteClick(
        navHostController: NavHostController,
        homeRoute: HomeRoute
    ) {
        navHostController.navigate(homeRoute) {
            launchSingleTop = true
            restoreState = true
            popUpTo(DevicesList) {
                saveState = true
            }
        }
    }

    private fun NavDestination?.toHomeRoute(): HomeRoute =
        when {
            this == null -> DevicesList
            hasRoute<DevicesList>() -> DevicesList
            hasRoute<AddDevice>() -> AddDevice
            hasRoute<UserSettings>() -> UserSettings
            else -> DevicesList
        }
}
