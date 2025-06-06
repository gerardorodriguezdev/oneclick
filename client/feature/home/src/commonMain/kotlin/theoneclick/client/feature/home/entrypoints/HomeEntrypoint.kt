package theoneclick.client.feature.home.entrypoints

import theoneclick.shared.core.models.routes.AppRoute
import theoneclick.shared.core.models.routes.HomeRoute

class HomeEntrypoint(private val coreComponent: CoreComponent) {

    fun NavGraphBuilder.home(navController: NavController) {
        navigation<AppRoute.Home>(startDestination = HomeRoute.NavigationBarRoute.DevicesList) {
            composable<HomeRoute.NavigationBarRoute.DevicesList> { navBackstackEntry ->
                val homeComponent = navController.getOrBuildHomeComponent(navBackstackEntry)
                val devicesListViewModel = viewModel { homeComponent.devicesListViewModelFactory() }

                DevicesListScreen(
                    state = devicesListViewModel.state.value,
                    onEvent = devicesListViewModel::onEvent,
                )
            }

            composable<HomeRoute.NavigationBarRoute.AddDevice> { navBackstackEntry ->
                val homeComponent = navController.getOrBuildHomeComponent(navBackstackEntry)
                val addDeviceViewModel = viewModel { homeComponent.addDeviceViewModelFactory() }

                AddDeviceScreen(
                    state = addDeviceViewModel.state.value,
                    onEvent = addDeviceViewModel::onEvent,
                )
            }

            composable<HomeRoute.NavigationBarRoute.UserSettings> { navBackstackEntry ->
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
        val parentEntry = remember(navBackstackEntry) { getBackStackEntry<AppRoute.Home>() }
        val homeViewModel = viewModel(parentEntry) { HomeViewModel(coreComponent) }
        return homeViewModel.homeComponent
    }

    private class HomeViewModel(coreComponent: CoreComponent) : ViewModel() {
        val homeComponent: HomeComponent = createHomeComponent(coreComponent)
    }
}