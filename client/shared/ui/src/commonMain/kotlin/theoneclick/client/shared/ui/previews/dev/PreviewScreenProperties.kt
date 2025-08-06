package theoneclick.client.shared.ui.previews.dev

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import theoneclick.client.shared.ui.screenProperties.LocalScreenProperties
import theoneclick.client.shared.ui.screenProperties.ScreenProperties

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
