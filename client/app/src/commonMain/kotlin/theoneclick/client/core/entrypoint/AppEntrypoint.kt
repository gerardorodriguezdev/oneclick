package theoneclick.client.core.entrypoint

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import theoneclick.client.core.di.AppModule
import theoneclick.client.core.di.CoreModule
import theoneclick.client.core.di.HomeModule
import theoneclick.client.core.extensions.RegisterNavigationControllerObserver
import theoneclick.client.core.extensions.modules
import theoneclick.client.core.platform.AppDependencies
import theoneclick.client.core.ui.screenProperties.LocalScreenProperties
import theoneclick.client.core.ui.screenProperties.ScreenProperties
import theoneclick.client.core.ui.screens.AppScreen
import theoneclick.client.core.ui.screens.AppScreenState
import theoneclick.client.core.ui.screens.LoadingScreen
import theoneclick.client.core.ui.screens.LoginScreen
import theoneclick.client.core.ui.theme.TheOneClickTheme
import theoneclick.client.core.viewModels.InitViewModel
import theoneclick.client.core.viewModels.LoginViewModel
import theoneclick.shared.core.models.routes.AppRoute.*
import theoneclick.shared.core.models.routes.HomeRoute
import theoneclick.shared.core.models.routes.HomeRoute.*

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
                        onNavigationBarClicked = { navigationBarRoute ->
                            navHostController.handleNavigationBarClick(navigationBarRoute)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding(),
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
                                homeRoutes(navHostController)
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

    private fun NavDestination?.toHomeRoute(): HomeRoute? =
        when {
            this == null -> null
            hasRoute<DevicesList>() -> DevicesList
            hasRoute<AddDevice>() -> AddDevice
            hasRoute<UserSettings>() -> UserSettings
            else -> null
        }

    @Composable
    private fun NavHostController.navigationBar(): AppScreenState.NavigationBar? {
        val navBackStackEntry by currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val selectedNavigationBarRoute = currentDestination.toHomeRoute()

        return if (selectedNavigationBarRoute != null) {
            val isCompact = LocalScreenProperties.current.isCompact
            if (isCompact) {
                AppScreenState.NavigationBar.Bottom(selectedNavigationBarRoute)
            } else {
                AppScreenState.NavigationBar.Start(selectedNavigationBarRoute)
            }
        } else null
    }

    private fun NavHostController.handleNavigationBarClick(navigationBarRoute: HomeRoute) {
        navigate(navigationBarRoute) {
            launchSingleTop = true
            restoreState = true
            popUpTo(DevicesList) {
                saveState = true
            }
        }
    }
}
