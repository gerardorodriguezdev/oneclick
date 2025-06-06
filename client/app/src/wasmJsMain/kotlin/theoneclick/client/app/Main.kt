package theoneclick.client.app

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToNavigation
import androidx.navigation.compose.rememberNavController
import io.ktor.client.engine.js.*
import kotlinx.browser.document
import kotlinx.browser.window
import theoneclick.client.app.buildkonfig.BuildKonfig
import theoneclick.client.app.di.AppComponent
import theoneclick.client.app.di.create
import theoneclick.client.app.entrypoints.AppEntrypoint
import theoneclick.client.shared.di.wasmCoreComponent
import theoneclick.client.shared.navigation.DefaultNavigationController
import theoneclick.client.shared.network.platform.WasmLogoutManager
import theoneclick.client.shared.notifications.DefaultNotificationsController
import theoneclick.shared.core.platform.EmptyAppLogger
import theoneclick.shared.core.platform.appLogger
import theoneclick.shared.dispatchers.platform.dispatchersProvider

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
    val appLogger = if (BuildKonfig.IS_DEBUG) appLogger() else EmptyAppLogger()
    val navigationController = DefaultNavigationController(appLogger)
    val coreComponent = wasmCoreComponent(
        httpClientEngine = Js.create(),
        appLogger = appLogger,
        dispatchersProvider = dispatchersProvider(),
        navigationController = navigationController,
        logoutManager = WasmLogoutManager(navigationController),
        notificationsController = DefaultNotificationsController(),
    )
    val appComponent = AppComponent::class.create(coreComponent)
    val appEntrypoint = AppEntrypoint(appComponent = appComponent, coreComponent = coreComponent)
    ComposeViewport(requireNotNull(document.body)) {
        val navHostController = rememberNavController()

        appEntrypoint.App(navHostController = navHostController)

        LaunchedEffect(Unit) {
            window.bindToNavigation(navHostController)
        }
    }
}
