package oneclick.client.app

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import oneclick.client.app.OneClickApplication.appEntrypoint

fun MainViewController(): UIViewController =
    ComposeUIViewController { appEntrypoint.App() }
