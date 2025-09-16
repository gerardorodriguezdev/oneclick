package theoneclick.client.app.entrypoints

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import theoneclick.client.app.di.AppComponent
import theoneclick.client.app.ui.screens.*
import theoneclick.client.features.home.entrypoints.HomeEntrypoint
import theoneclick.client.shared.di.CoreComponent
import theoneclick.client.shared.navigation.RegisterNavigationControllerObserver
import theoneclick.client.shared.navigation.models.routes.AppRoute.Init
import theoneclick.client.shared.navigation.models.routes.AppRoute.Login
import theoneclick.client.shared.navigation.models.routes.HomeRoute
import theoneclick.client.shared.navigation.models.routes.HomeRoute.HomesList
import theoneclick.client.shared.notifications.NotificationsController.Notification
import theoneclick.client.shared.ui.screenProperties.LocalScreenProperties
import theoneclick.client.shared.ui.screenProperties.ScreenProperties
import theoneclick.client.shared.ui.theme.TheOneClickTheme
import kotlin.reflect.KClass

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
                            val initViewModel = viewModel { appComponent.initViewModelFactory() }
                            LoadingScreen()
                        }

                        composable<Login> {
                            val loginViewModel = viewModel { appComponent.loginViewModelFactory() }
                            val state by loginViewModel.loginScreenState.collectAsState()
                            LoginScreen(
                                state = state,
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
        val selectedNavigationBarRoute = NavigationBarRoute.entries.firstOrNull { navigationBarRoute ->
            currentDestination.isRouteInHierarchy(navigationBarRoute.route)
        }
        return selectedNavigationBarRoute?.let {
            val isCompact = LocalScreenProperties.current.isCompact
            if (isCompact) {
                AppScreenState.NavigationBar.Bottom(selectedNavigationBarRoute)
            } else {
                AppScreenState.NavigationBar.Start(selectedNavigationBarRoute)
            }
        }
    }

    private fun NavHostController.handleNavigationBarClick(navigationBarRoute: NavigationBarRoute) {
        val destination = when (navigationBarRoute) {
            NavigationBarRoute.HOMES_LIST -> HomesList
            NavigationBarRoute.USER_SETTINGS -> HomeRoute.UserSettings
        }

        navigate(destination) {
            launchSingleTop = true
            restoreState = true
            popUpTo(HomesList) {
                saveState = true
            }
        }
    }

    private fun NavDestination?.isRouteInHierarchy(route: KClass<*>): Boolean =
        this?.hierarchy?.any { destination -> destination.hasRoute(route) } ?: false

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
