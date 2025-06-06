package theoneclick.client.shared.ui.previews.dev

import androidx.compose.runtime.Composable
import theoneclick.client.shared.ui.previews.providers.base.PreviewModel
import theoneclick.client.shared.ui.theme.TheOneClickTheme

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
