package theoneclick.client.features.home.entrypoints

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import theoneclick.client.features.home.di.HomeComponent
import theoneclick.client.features.home.di.createHomeComponent
import theoneclick.client.features.home.ui.screens.AddDeviceScreen
import theoneclick.client.features.home.ui.screens.DevicesListScreen
import theoneclick.client.features.home.ui.screens.UserSettingsScreen
import theoneclick.client.shared.di.CoreComponent
import theoneclick.shared.core.models.routes.AppRoute.Home
import theoneclick.shared.core.models.routes.HomeRoute.NavigationBarRoute

class HomeEntrypoint(private val coreComponent: CoreComponent) {

    fun NavGraphBuilder.home(navController: NavController) {
        navigation<Home>(startDestination = NavigationBarRoute.DevicesList) {
            composable<NavigationBarRoute.DevicesList> { navBackstackEntry ->
                val homeComponent = navController.getOrBuildHomeComponent(navBackstackEntry)
                val devicesListViewModel = viewModel { homeComponent.devicesListViewModelFactory() }

                DevicesListScreen(
                    state = devicesListViewModel.state.value,
                    onEvent = devicesListViewModel::onEvent,
                )
            }

            composable<NavigationBarRoute.AddDevice> { navBackstackEntry ->
                val homeComponent = navController.getOrBuildHomeComponent(navBackstackEntry)
                val addDeviceViewModel = viewModel { homeComponent.addDeviceViewModelFactory() }

                AddDeviceScreen(
                    state = addDeviceViewModel.state.value,
                    onEvent = addDeviceViewModel::onEvent,
                )
            }

            composable<NavigationBarRoute.UserSettings> { navBackstackEntry ->
                val homeComponent = navController.getOrBuildHomeComponent(navBackstackEntry)
                val userSettingsViewModel = viewModel { homeComponent.userSettingsViewModelFactory() }

                UserSettingsScreen(
                    state = userSettingsViewModel.state.value,
                    onEvent = userSettingsViewModel::onEvent,
                )
            }
        }
    }

    @Composable
    private fun NavController.getOrBuildHomeComponent(navBackstackEntry: NavBackStackEntry): HomeComponent {
        val parentEntry = remember(navBackstackEntry) { getBackStackEntry<Home>() }
        val homeViewModel = viewModel(parentEntry) { HomeViewModel(coreComponent) }
        return homeViewModel.homeComponent
    }

    private class HomeViewModel(coreComponent: CoreComponent) : ViewModel() {
        val homeComponent: HomeComponent = createHomeComponent(coreComponent)
    }
}