package oneclick.client.apps.features.home.entrypoints

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import oneclick.client.apps.features.home.di.HomeComponent
import oneclick.client.apps.features.home.di.createHomeComponent
import oneclick.client.apps.features.home.ui.screens.HomesListScreen
import oneclick.client.apps.features.home.ui.screens.UserSettingsScreen
import oneclick.client.shared.di.CoreComponent
import oneclick.client.shared.navigation.models.routes.AppRoute.Home
import oneclick.client.shared.navigation.models.routes.HomeRoute.HomesList
import oneclick.client.shared.navigation.models.routes.HomeRoute.UserSettings

class HomeEntrypoint(private val coreComponent: CoreComponent) {

    fun NavGraphBuilder.home(navController: NavController) {
        navigation<Home>(startDestination = HomesList) {
            composable<HomesList> { navBackstackEntry ->
                val homeComponent = navController.getOrBuildHomeComponent(navBackstackEntry)
                val homesListViewModel = viewModel { homeComponent.homesListViewModelFactory() }
                val state by homesListViewModel.homesListScreenState.collectAsState()

                HomesListScreen(
                    state = state,
                    onEvent = homesListViewModel::onEvent,
                )
            }

            composable<UserSettings> { navBackstackEntry ->
                val homeComponent = navController.getOrBuildHomeComponent(navBackstackEntry)
                val userSettingsViewModel = viewModel { homeComponent.userSettingsViewModelFactory() }
                val state by userSettingsViewModel.userSettingsScreenState.collectAsState()

                UserSettingsScreen(
                    state = state,
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
        val homeComponent: HomeComponent =
            createHomeComponent(coreComponent)
    }
}
