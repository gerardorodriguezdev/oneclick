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
import theoneclick.client.core.entrypoint.HomeEntrypoint.Companion.createHomeEntrypoint
import theoneclick.client.core.extensions.RegisterNavigationControllerObserver
import theoneclick.client.core.platform.AppDependencies
import theoneclick.client.core.ui.screenProperties.ScreenProperties
import theoneclick.client.core.ui.screens.LoadingScreen
import theoneclick.client.core.ui.screens.LoginScreen
import theoneclick.client.core.ui.theme.TheOneClickTheme
import theoneclick.client.core.viewModels.InitViewModel
import theoneclick.client.core.viewModels.LoginViewModel
import theoneclick.shared.core.models.routes.AppRoute.*

class AppEntrypoint(
    appDependencies: AppDependencies,
    skipStartKoin: Boolean = false,
) {
    private val homeEntrypoint = createHomeEntrypoint()
    //TODO: Other
    val koinModules = {
        val coreModule = buildCoreModule(appDependencies)
        val appModule = buildAppModule(coreModule)
        val loggedModule = buildLoggedModule(coreModule)
        listOf(coreModule, appModule, loggedModule)
    }.invoke()

    init {
        if (!skipStartKoin) {
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
                            homeEntrypoint.HomeScreen()
                        }
                    }
                }
            }
        }
    }
}
