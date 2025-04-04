package theoneclick.client.core

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.ktor.client.engine.js.*
import kotlinx.browser.document
import theoneclick.client.core.buildkonfig.BuildKonfig
import theoneclick.client.core.entrypoint.AppEntrypoint
import theoneclick.client.core.navigation.RealNavigationController
import theoneclick.client.core.platform.WasmAppDependencies
import theoneclick.shared.core.platform.EmptyAppLogger
import theoneclick.shared.core.platform.appLogger
import theoneclick.shared.dispatchers.platform.dispatchersProvider

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val appLogger = if (BuildKonfig.IS_DEBUG) appLogger() else EmptyAppLogger()
    val appEntrypoint = AppEntrypoint(
        WasmAppDependencies(
            httpClientEngine = Js.create(),
            appLogger = appLogger,
            dispatchersProvider = dispatchersProvider(),
            navigationController = RealNavigationController(appLogger)
        )
    )
    ComposeViewport(requireNotNull(document.body)) {
        appEntrypoint.App()
    }
}
