package oneclick.client.shared.ui.previews.dev

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import oneclick.client.shared.ui.previews.providers.base.PreviewModel
import oneclick.client.shared.ui.theme.OneClickTheme

@Composable
fun <T> ComponentPreviewComposable(
    previewModel: PreviewModel<T>,
    content: @Composable () -> Unit
) {
    OneClickTheme(isDarkTheme = previewModel.isDarkTheme) {
        PreviewScreenProperties(isCompact = previewModel.isCompact) {
            val backgroundColor = if (previewModel.isDarkTheme) Color.Black else Color.White

            Box(modifier = Modifier.background(color = backgroundColor)) {
                content()
            }
        }
    }
}
