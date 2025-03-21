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
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import theoneclick.client.core.scopeIdGenerator.StaticLoggedScopeIdGenerator
import theoneclick.client.core.ui.screens.homeScreen.AddDeviceScreen
import theoneclick.client.core.ui.screens.homeScreen.DevicesListScreen
import theoneclick.client.core.ui.screens.homeScreen.HomeScreenScaffold
import theoneclick.client.core.viewModels.homeScreen.AddDeviceViewModel
import theoneclick.client.core.viewModels.homeScreen.DevicesListViewModel
import theoneclick.client.core.viewModels.homeScreen.HomeViewModel
import theoneclick.client.core.dataSources.LoggedDataSource
import theoneclick.client.core.dataSources.RemoteLoggedDataSource
import theoneclick.shared.core.models.routes.HomeRoute
import theoneclick.shared.core.models.routes.HomeRoute.AddDevice
import theoneclick.shared.core.models.routes.HomeRoute.DevicesList

class HomeEntrypoint {

    // Only visible for testing
    fun buildLoggedModule(coreModule: Module): Module =
        module {
            includes(coreModule)

            val loggedScopeIdGenerator = StaticLoggedScopeIdGenerator()

            scope<HomeViewModel> {
                scopedOf(::RemoteLoggedDataSource) bind LoggedDataSource::class
            }

            viewModel {
                HomeViewModel(scopeId = loggedScopeIdGenerator.scopeId())
            }

            viewModel {
                DevicesListViewModel(
                    loggedDataSource = getScope(loggedScopeIdGenerator.scopeId()).get(),
                    navigationController = get(),
                )
            }

            viewModel {
                AddDeviceViewModel(
                    loggedDataSource = getScope(loggedScopeIdGenerator.scopeId()).get(),
                    navigationController = get(),
                )
            }
        }

    @Composable
    fun HomeScreen(navHostController: NavHostController = rememberNavController()) {
        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val selectedHomeRoute = currentDestination.toHomeRoute()
        val homeViewModel: HomeViewModel = koinViewModel()

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
            else -> DevicesList
        }
}
