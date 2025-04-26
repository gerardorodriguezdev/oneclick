package theoneclick.client.core.ui.previews.dev

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import theoneclick.client.core.ui.screenProperties.LocalScreenProperties
import theoneclick.client.core.ui.screenProperties.ScreenProperties

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
