package oneclick.client.apps.user.ui.previews.dev

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import oneclick.client.apps.user.ui.screenProperties.LocalScreenProperties
import oneclick.client.apps.user.ui.screenProperties.ScreenProperties

@Composable
fun PreviewScreenProperties(
    isCompact: Boolean,
    content: @Composable () -> Unit,
) {
    val screenProperties = ScreenProperties(isCompact = isCompact)
    CompositionLocalProvider(LocalScreenProperties provides screenProperties) {
        content()
    }
}
