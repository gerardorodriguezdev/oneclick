package theoneclick.client.app.entrypoint

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import org.koin.compose.viewmodel.koinViewModel
import theoneclick.client.app.di.HomeModule.Companion.HOME_SCOPE
import theoneclick.client.app.extensions.getOrCreateScope
import theoneclick.client.app.ui.screens.homeScreen.AddDeviceScreen
import theoneclick.client.app.ui.screens.homeScreen.DevicesListScreen
import theoneclick.client.app.ui.screens.homeScreen.UserSettingsScreen
import theoneclick.client.app.viewModels.homeScreen.AddDeviceViewModel
import theoneclick.client.app.viewModels.homeScreen.DevicesListViewModel
import theoneclick.client.app.viewModels.homeScreen.UserSettingsViewModel
import theoneclick.shared.core.models.routes.HomeRoute.NavigationBarRoute.*

fun NavGraphBuilder.home(navHostController: NavHostController) {
    composable<DevicesList> {
        val scope = navHostController.getOrCreateScope(HOME_SCOPE)
        val devicesListViewModel: DevicesListViewModel =
            koinViewModel(scope = scope)
        DevicesListScreen(
            state = devicesListViewModel.state.value,
            onEvent = devicesListViewModel::onEvent,
        )
    }

    composable<AddDevice> {
        val scope = navHostController.getOrCreateScope(HOME_SCOPE)
        val addDeviceViewModel: AddDeviceViewModel = koinViewModel(scope = scope)
        AddDeviceScreen(
            state = addDeviceViewModel.state.value,
            onEvent = addDeviceViewModel::onEvent,
        )
    }

    composable<UserSettings> {
        val scope = navHostController.getOrCreateScope(HOME_SCOPE)
        val userSettingsViewModel: UserSettingsViewModel =
            koinViewModel(scope = scope)

        UserSettingsScreen(
            state = userSettingsViewModel.state.value,
            onEvent = userSettingsViewModel::onEvent,
        )
    }
}
