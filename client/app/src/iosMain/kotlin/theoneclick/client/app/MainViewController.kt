package theoneclick.client.app

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import theoneclick.client.app.TheOneClickApplication.appEntrypoint

fun MainViewController(): UIViewController =
    ComposeUIViewController { appEntrypoint.App() }