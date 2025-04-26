package theoneclick.client.core.ui.previews.dev

import androidx.compose.runtime.Composable
import theoneclick.client.core.ui.previews.providers.base.PreviewModel
import theoneclick.client.core.ui.theme.TheOneClickTheme

@Composable
fun <T> ScreenPreviewComposable(
    previewModel: PreviewModel<T>,
    content: @Composable () -> Unit
) {
    TheOneClickTheme(isDarkTheme = previewModel.isDarkTheme) {
        PreviewScreenProperties(isCompact = previewModel.isCompact) {
            content()
        }
    }
}
