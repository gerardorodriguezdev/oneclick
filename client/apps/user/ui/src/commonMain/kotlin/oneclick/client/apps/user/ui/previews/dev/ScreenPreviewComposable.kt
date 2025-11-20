package oneclick.client.apps.user.ui.previews.dev

import androidx.compose.runtime.Composable
import oneclick.client.apps.user.ui.previews.providers.base.PreviewModel
import oneclick.client.apps.user.ui.theme.OneClickTheme

@Composable
fun <T> ScreenPreviewComposable(
    previewModel: PreviewModel<T>,
    content: @Composable () -> Unit
) {
    OneClickTheme(isDarkTheme = previewModel.isDarkTheme) {
        PreviewScreenProperties(isCompact = previewModel.isCompact) {
            content()
        }
    }
}
