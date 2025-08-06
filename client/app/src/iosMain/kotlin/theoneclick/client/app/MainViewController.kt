package theoneclick.client.app

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import theoneclick.client.app.entrypoints.AppEntrypoint

fun MainViewController(appEntrypoint: AppEntrypoint): UIViewController =
    ComposeUIViewController { appEntrypoint.App() }