package oneclick.client.apps.user.core

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import androidx.navigation.compose.rememberNavController
import io.ktor.client.engine.js.*
import kotlinx.browser.document
import oneclick.client.apps.user.core.buildkonfig.BuildKonfig
import oneclick.client.apps.user.core.di.createAppComponent
import oneclick.client.apps.user.core.entrypoints.AppEntrypoint
import oneclick.client.shared.di.wasmCoreComponent
import oneclick.client.shared.navigation.DefaultNavigationController
import oneclick.client.shared.notifications.DefaultNotificationsController
import oneclick.shared.dispatchers.platform.dispatchersProvider
import oneclick.shared.logging.EmptyAppLogger
import oneclick.shared.logging.appLogger

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
    val appLogger = if (BuildKonfig.IS_DEBUG) appLogger() else EmptyAppLogger()
    val navigationController = DefaultNavigationController()
    val coreComponent = wasmCoreComponent(
        httpClientEngine = Js.create(),
        appLogger = appLogger,
        dispatchersProvider = dispatchersProvider(),
        navigationController = navigationController,
        logoutManager = WasmLogoutManager(navigationController),
        notificationsController = DefaultNotificationsController(),
    )
    val appComponent = createAppComponent(coreComponent)
    val appEntrypoint = AppEntrypoint(appComponent = appComponent, coreComponent = coreComponent)
    ComposeViewport(requireNotNull(document.body)) {
        val navHostController = rememberNavController()

        appEntrypoint.App(navHostController = navHostController)

        LaunchedEffect(Unit) {
            navHostController.bindToBrowserNavigation()
        }
    }
}
