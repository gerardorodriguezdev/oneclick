package theoneclick.client.app.entrypoint

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import theoneclick.client.app.di.AppComponent
import theoneclick.client.app.ui.screenProperties.LocalScreenProperties
import theoneclick.client.app.ui.screenProperties.ScreenProperties
import theoneclick.client.app.ui.screens.AppScreen
import theoneclick.client.app.ui.screens.AppScreenState
import theoneclick.client.app.ui.screens.LoadingScreen
import theoneclick.client.app.ui.screens.LoginScreen
import theoneclick.client.app.ui.theme.TheOneClickTheme
import theoneclick.client.app.viewModels.InitViewModel
import theoneclick.client.app.viewModels.LoginViewModel
import theoneclick.client.shared.di.CoreComponent
import theoneclick.client.shared.navigation.RegisterNavigationControllerObserver
import theoneclick.shared.core.models.routes.AppRoute.*
import theoneclick.shared.core.models.routes.HomeRoute.NavigationBarRoute
import theoneclick.shared.core.models.routes.HomeRoute.NavigationBarRoute.*

class AppEntrypoint(
    private val coreComponent: CoreComponent,
    private val appComponent: AppComponent,
) {

    @Composable
    fun App(
        isDarkTheme: Boolean = isSystemInDarkTheme(),
        navHostController: NavHostController = rememberNavController(),
    ) {
        TheOneClickTheme(isDarkTheme = isDarkTheme) {
            ScreenProperties {
                RegisterNavigationControllerObserver(
                    navigationController = coreComponent.navigationController,
                    navHostController = navHostController
                )

                AppScreen(
                    state = AppScreenState(
                        navigationBar = navHostController.navigationBar(),
                    ),
                    onNavigationBarClick = { navigationBarRoute ->
                        navHostController.handleNavigationBarClick(navigationBarRoute)
                    },
                ) {
                    NavHost(
                        navController = navHostController,
                        startDestination = Init,
                    ) {
                        composable<Init> {
                            @Suppress("UnusedPrivateProperty")
                            val initViewModel: InitViewModel = viewModel { appComponent.initViewModelFactory() }
                            LoadingScreen()
                        }

                        composable<Login> {
                            val loginViewModel: LoginViewModel = viewModel { appComponent.loginViewModelFactory() }
                            LoginScreen(
                                state = loginViewModel.state.value,
                                onEvent = loginViewModel::onEvent,
                            )
                        }

                        navigation<Home>(startDestination = DevicesList) {
                            val homeEntrypoint = HomeEntrypoint(
                                navController = navHostController,
                                coreComponent = coreComponent
                            )
                            with(homeEntrypoint) {
                                home()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun NavDestination.toNavigationBarRoute(): NavigationBarRoute? =
        when {
            hasRoute<DevicesList>() -> DevicesList
            hasRoute<AddDevice>() -> AddDevice
            hasRoute<UserSettings>() -> UserSettings
            else -> null
        }

    @Composable
    private fun NavHostController.navigationBar(): AppScreenState.NavigationBar? {
        val navBackStackEntry by currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val selectedNavigationBarRoute = currentDestination?.toNavigationBarRoute()

        return if (selectedNavigationBarRoute != null) {
            val isCompact = LocalScreenProperties.current.isCompact
            if (isCompact) {
                AppScreenState.NavigationBar.Bottom(selectedNavigationBarRoute)
            } else {
                AppScreenState.NavigationBar.Start(selectedNavigationBarRoute)
            }
        } else {
            null
        }
    }

    private fun NavHostController.handleNavigationBarClick(navigationBarRoute: NavigationBarRoute) {
        navigate(navigationBarRoute) {
            launchSingleTop = true
            restoreState = true
            popUpTo(DevicesList) {
                saveState = true
            }
        }
    }
}