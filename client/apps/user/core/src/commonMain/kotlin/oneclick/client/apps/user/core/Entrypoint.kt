package oneclick.client.apps.user.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import oneclick.client.apps.features.home.entrypoints.HomeEntrypoint
import oneclick.client.apps.user.core.di.AppComponent
import oneclick.client.apps.user.core.ui.screens.*
import oneclick.client.apps.user.di.CoreComponent
import oneclick.client.apps.user.navigation.RegisterNavigationControllerObserver
import oneclick.client.apps.user.navigation.models.routes.AppRoute
import oneclick.client.apps.user.navigation.models.routes.HomeRoute
import oneclick.client.apps.user.notifications.NotificationsController
import oneclick.client.apps.user.ui.screenProperties.LocalScreenProperties
import oneclick.client.apps.user.ui.screenProperties.ScreenProperties
import oneclick.client.apps.user.ui.theme.OneClickTheme
import kotlin.reflect.KClass

class Entrypoint(
    private val coreComponent: CoreComponent,
    private val appComponent: AppComponent,
) {
    private val homeEntrypoint =
        HomeEntrypoint(coreComponent = coreComponent)

    @Composable
    fun App(
        isDarkTheme: Boolean = isSystemInDarkTheme(),
        navHostController: NavHostController = rememberNavController(),
    ) {
        OneClickTheme(isDarkTheme = isDarkTheme) {
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
                        startDestination = AppRoute.Init,
                    ) {
                        composable<AppRoute.Init> {
                            val initViewModel = viewModel { appComponent.initViewModelFactory() }
                            LoadingScreen()
                        }

                        composable<AppRoute.Login> {
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
            val isCompact =
                LocalScreenProperties.current.isCompact
            if (isCompact) {
                AppScreenState.NavigationBar.Bottom(selectedNavigationBarRoute)
            } else {
                AppScreenState.NavigationBar.Start(selectedNavigationBarRoute)
            }
        }
    }

    private fun NavHostController.handleNavigationBarClick(navigationBarRoute: NavigationBarRoute) {
        val destination = when (navigationBarRoute) {
            NavigationBarRoute.HOMES_LIST -> HomeRoute.HomesList
            NavigationBarRoute.USER_SETTINGS -> HomeRoute.UserSettings
        }

        navigate(destination) {
            launchSingleTop = true
            restoreState = true
            popUpTo(HomeRoute.HomesList) {
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
            is NotificationsController.Notification.Success ->
                AppScreenState.SnackbarState(
                    text = notification.message,
                    isError = false
                )

            is NotificationsController.Notification.Error ->
                AppScreenState.SnackbarState(
                    text = notification.message,
                    isError = true
                )
        }
    }
}