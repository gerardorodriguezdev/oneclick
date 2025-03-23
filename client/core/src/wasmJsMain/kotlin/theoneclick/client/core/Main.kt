package theoneclick.client.core

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import theoneclick.client.core.entrypoint.AppEntrypoint
import theoneclick.client.core.platform.WasmAppDependencies

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val appEntrypoint = AppEntrypoint()
    appEntrypoint.startKoin(appDependencies = WasmAppDependencies())

    ComposeViewport(requireNotNull(document.body)) {
        appEntrypoint.App()
    }
}
