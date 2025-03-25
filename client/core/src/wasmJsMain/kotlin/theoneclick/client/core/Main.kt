package theoneclick.client.core

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.ktor.client.engine.js.*
import kotlinx.browser.document
import theoneclick.client.core.entrypoint.AppEntrypoint
import theoneclick.client.core.navigation.RealNavigationController
import theoneclick.client.core.platform.WasmAppDependencies
import theoneclick.shared.dispatchers.platform.dispatchersProvider

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val appEntrypoint = AppEntrypoint()
    appEntrypoint.startKoin(
        appDependencies = WasmAppDependencies(
            httpClientEngine = Js.create(),
            dispatchersProvider = dispatchersProvider(),
            navigationController = RealNavigationController()
        )
    )

    ComposeViewport(requireNotNull(document.body)) {
        appEntrypoint.App()
    }
}
