package theoneclick.client.core

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToNavigation
import androidx.navigation.compose.rememberNavController
import io.ktor.client.engine.js.*
import kotlinx.browser.document
import kotlinx.browser.window
import theoneclick.client.core.buildkonfig.BuildKonfig
import theoneclick.client.core.entrypoint.AppEntrypoint
import theoneclick.client.core.navigation.RealNavigationController
import theoneclick.client.core.platform.WasmAppDependencies
import theoneclick.client.core.platform.WasmLogoutManager
import theoneclick.shared.core.platform.EmptyAppLogger
import theoneclick.shared.core.platform.appLogger
import theoneclick.shared.dispatchers.platform.dispatchersProvider

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
    val appLogger = if (BuildKonfig.IS_DEBUG) appLogger() else EmptyAppLogger()
    val navigationController = RealNavigationController(appLogger)
    val appEntrypoint = AppEntrypoint(
        WasmAppDependencies(
            httpClientEngine = Js.create(),
            appLogger = appLogger,
            dispatchersProvider = dispatchersProvider(),
            navigationController = navigationController,
            logoutManager = WasmLogoutManager(navigationController)
        )
    )
    ComposeViewport(requireNotNull(document.body)) {
        val navHostController = rememberNavController()

        appEntrypoint.App(navHostController = navHostController)

        LaunchedEffect(Unit) {
            window.bindToNavigation(navHostController)
        }
    }
}
