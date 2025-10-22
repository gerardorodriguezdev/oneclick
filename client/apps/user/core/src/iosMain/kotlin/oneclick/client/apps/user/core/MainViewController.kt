package oneclick.client.apps.user.core

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import oneclick.client.apps.user.core.OneClickApplication.appEntrypoint

fun MainViewController(): UIViewController =
    ComposeUIViewController { appEntrypoint.App() }
