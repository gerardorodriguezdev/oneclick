package theoneclick.client.app.entrypoints

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import theoneclick.client.app.di.AppComponent
import theoneclick.client.app.ui.screens.AppScreen
import theoneclick.client.app.ui.screens.AppScreenState
import theoneclick.client.app.ui.screens.LoadingScreen
import theoneclick.client.app.ui.screens.LoginScreen
import theoneclick.client.features.home.entrypoints.HomeEntrypoint
import theoneclick.client.shared.di.CoreComponent
import theoneclick.client.shared.navigation.RegisterNavigationControllerObserver
import theoneclick.client.shared.notifications.NotificationsController.Notification
import theoneclick.client.shared.ui.screenProperties.LocalScreenProperties
import theoneclick.client.shared.ui.screenProperties.ScreenProperties
import theoneclick.client.shared.ui.theme.TheOneClickTheme
import theoneclick.shared.core.models.routes.AppRoute.Init
import theoneclick.shared.core.models.routes.AppRoute.Login
import theoneclick.shared.core.models.routes.HomeRoute.NavigationBarRoute
import theoneclick.shared.core.models.routes.HomeRoute.NavigationBarRoute.*

class AppEntrypoint(
    private val coreComponent: CoreComponent,
    private val appComponent: AppComponent,
) {
    private val homeEntrypoint = HomeEntrypoint(coreComponent = coreComponent)

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

                val coroutineScope = rememberCoroutineScope()
                AppScreen(
                    state = AppScreenState(
                        navigationBar = navHostController.navigationBar(),
                        snackbarState = snackbarState(),
                    ),
                    onNavigationBarClick = { navigationBarRoute ->
                        navHostController.handleNavigationBarClick(navigationBarRoute)
                    },
                    onSnackbarShown = {
                        coroutineScope.launch {
                            coreComponent.notificationsController.clearNotifications()
                        }
                    }
                ) {
                    NavHost(
                        navController = navHostController,
                        startDestination = Init,
                    ) {
                        composable<Init> {
                            @Suppress("UnusedPrivateProperty")
                            val initViewModel = viewModel { appComponent.initViewModelFactory() }
                            LoadingScreen()
                        }

                        composable<Login> {
                            val loginViewModel = viewModel { appComponent.loginViewModelFactory() }
                            val state = loginViewModel.loginScreenState.collectAsState()
                            LoginScreen(
                                state = state.value,
                                onEvent = loginViewModel::onEvent,
                            )
                        }

                        with(homeEntrypoint) {
                            home(navHostController)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun NavHostController.navigationBar(): AppScreenState.NavigationBar? {
        val navBackStackEntry by currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val selectedNavigationBarRoute = currentDestination?.toNavigationBarRoute()

        return selectedNavigationBarRoute?.let {
            val isCompact = LocalScreenProperties.current.isCompact
            if (isCompact) {
                AppScreenState.NavigationBar.Bottom(selectedNavigationBarRoute)
            } else {
                AppScreenState.NavigationBar.Start(selectedNavigationBarRoute)
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

    private fun NavHostController.handleNavigationBarClick(navigationBarRoute: NavigationBarRoute) {
        navigate(navigationBarRoute) {
            launchSingleTop = true
            restoreState = true
            popUpTo(DevicesList) {
                saveState = true
            }
        }
    }

    @Composable
    private fun snackbarState(): AppScreenState.SnackbarState? {
        val notification = coreComponent.notificationsController.notificationEvents.collectAsState(null).value
        return when (notification) {
            null -> null
            is Notification.Success ->
                AppScreenState.SnackbarState(
                    text = notification.message,
                    isError = false
                )

            is Notification.Error ->
                AppScreenState.SnackbarState(
                    text = notification.message,
                    isError = true
                )
        }
    }
}