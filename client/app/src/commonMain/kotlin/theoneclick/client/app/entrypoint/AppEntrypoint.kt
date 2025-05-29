package theoneclick.client.app.entrypoint

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import theoneclick.client.app.di.AppModule
import theoneclick.client.app.di.CoreModule
import theoneclick.client.app.di.HomeModule
import theoneclick.client.app.extensions.RegisterNavigationControllerObserver
import theoneclick.client.app.extensions.modules
import theoneclick.client.app.platform.AppDependencies
import theoneclick.client.app.ui.screenProperties.LocalScreenProperties
import theoneclick.client.app.ui.screenProperties.ScreenProperties
import theoneclick.client.app.ui.screens.AppScreen
import theoneclick.client.app.ui.screens.AppScreenState
import theoneclick.client.app.ui.screens.LoadingScreen
import theoneclick.client.app.ui.screens.LoginScreen
import theoneclick.client.app.ui.theme.TheOneClickTheme
import theoneclick.client.app.viewModels.InitViewModel
import theoneclick.client.app.viewModels.LoginViewModel
import theoneclick.shared.core.models.routes.AppRoute.*
import theoneclick.shared.core.models.routes.HomeRoute.NavigationBarRoute
import theoneclick.shared.core.models.routes.HomeRoute.NavigationBarRoute.*

class AppEntrypoint(
    appDependencies: AppDependencies,
    startKoin: Boolean = true,
) {
    val koinModules = koinModules(appDependencies)

    init {
        if (startKoin) {
            startKoin {
                modules(koinModules)
            }
        }
    }

    @OptIn(KoinExperimentalAPI::class)
    @Composable
    fun App(
        isDarkTheme: Boolean = isSystemInDarkTheme(),
        navHostController: NavHostController = rememberNavController(),
    ) {
        KoinContext {
            TheOneClickTheme(isDarkTheme = isDarkTheme) {
                ScreenProperties {
                    RegisterNavigationControllerObserver(
                        navigationController = koinInject(),
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
                                val initViewModel: InitViewModel = koinViewModel()
                                LoadingScreen()
                            }

                            composable<Login> {
                                val loginViewModel: LoginViewModel = koinViewModel()
                                LoginScreen(
                                    state = loginViewModel.state.value,
                                    onEvent = loginViewModel::onEvent,
                                )
                            }

                            navigation<Home>(startDestination = DevicesList) {
                                home(navHostController)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun koinModules(appDependencies: AppDependencies): List<Module> {
        val coreModule = CoreModule(appDependencies)
        val appModule = AppModule(coreModule)
        val homeModule = HomeModule(coreModule)
        return listOf(coreModule, appModule, homeModule).modules()
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
