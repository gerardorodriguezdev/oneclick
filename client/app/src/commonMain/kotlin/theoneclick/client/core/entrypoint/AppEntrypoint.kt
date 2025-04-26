package theoneclick.client.core.entrypoint

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import theoneclick.client.core.di.AppModule
import theoneclick.client.core.di.CoreModule
import theoneclick.client.core.di.HomeModule
import theoneclick.client.core.extensions.RegisterNavigationControllerObserver
import theoneclick.client.core.extensions.modules
import theoneclick.client.core.platform.AppDependencies
import theoneclick.client.core.ui.screenProperties.ScreenProperties
import theoneclick.client.core.ui.screens.LoadingScreen
import theoneclick.client.core.ui.screens.LoginScreen
import theoneclick.client.core.ui.screens.homeScreen.HomeScreen
import theoneclick.client.core.ui.theme.TheOneClickTheme
import theoneclick.client.core.viewModels.InitViewModel
import theoneclick.client.core.viewModels.LoginViewModel
import theoneclick.shared.core.models.routes.AppRoute.*

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

                    NavHost(
                        navController = navHostController,
                        startDestination = Init,
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding()
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

                        composable<Home> {
                            HomeScreen()
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
}
