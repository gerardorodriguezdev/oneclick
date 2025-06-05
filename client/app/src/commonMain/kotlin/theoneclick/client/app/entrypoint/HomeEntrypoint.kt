package theoneclick.client.app.entrypoint

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import theoneclick.client.app.di.HomeComponent
import theoneclick.client.app.di.createHomeComponent
import theoneclick.client.app.ui.screens.homeScreen.AddDeviceScreen
import theoneclick.client.app.ui.screens.homeScreen.DevicesListScreen
import theoneclick.client.app.ui.screens.homeScreen.UserSettingsScreen
import theoneclick.client.app.viewModels.homeScreen.AddDeviceViewModel
import theoneclick.client.app.viewModels.homeScreen.DevicesListViewModel
import theoneclick.client.app.viewModels.homeScreen.UserSettingsViewModel
import theoneclick.client.shared.di.CoreComponent
import theoneclick.shared.core.models.routes.AppRoute.Home
import theoneclick.shared.core.models.routes.HomeRoute.NavigationBarRoute.*

class HomeEntrypoint(
    private val navController: NavController,
    private val coreComponent: CoreComponent,
) {

    fun NavGraphBuilder.home() {
        composable<DevicesList> { navBackstackEntry ->
            val homeComponent = getOrBuildHomeComponent(navBackstackEntry)
            val devicesListViewModel: DevicesListViewModel = viewModel { homeComponent.devicesListViewModelFactory() }

            DevicesListScreen(
                state = devicesListViewModel.state.value,
                onEvent = devicesListViewModel::onEvent,
            )
        }

        composable<AddDevice> { navBackstackEntry ->
            val homeComponent = getOrBuildHomeComponent(navBackstackEntry)
            val addDeviceViewModel: AddDeviceViewModel = viewModel { homeComponent.addDeviceViewModelFactory() }

            AddDeviceScreen(
                state = addDeviceViewModel.state.value,
                onEvent = addDeviceViewModel::onEvent,
            )
        }

        composable<UserSettings> { navBackstackEntry ->
            val homeComponent = getOrBuildHomeComponent(navBackstackEntry)
            val userSettingsViewModel: UserSettingsViewModel =
                viewModel { homeComponent.userSettingsViewModelFactory() }

            UserSettingsScreen(
                state = userSettingsViewModel.state.value,
                onEvent = userSettingsViewModel::onEvent,
            )
        }
    }

    @Composable
    private fun getOrBuildHomeComponent(navBackstackEntry: NavBackStackEntry): HomeComponent {
        val parentEntry = remember(navBackstackEntry) {
            navController.getBackStackEntry<Home>()
        }
        val homeViewModel = viewModel(parentEntry) { HomeViewModel(coreComponent) }
        return homeViewModel.homeComponent
    }

    private class HomeViewModel(coreComponent: CoreComponent) : ViewModel() {
        val homeComponent: HomeComponent = createHomeComponent(coreComponent)
    }
}