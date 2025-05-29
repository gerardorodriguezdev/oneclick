@file:Suppress("UnusedPrivateMember")

package theoneclick.client.app.ui.previews.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.app.ui.previews.annotations.PreviewCompactScreen
import theoneclick.client.app.ui.previews.annotations.PreviewLargeScreen
import theoneclick.client.app.ui.previews.providers.base.PreviewModel
import theoneclick.client.app.ui.previews.providers.screens.AppScreenPreviewModels
import theoneclick.client.app.ui.screens.AppScreenPreview
import theoneclick.client.app.ui.screens.AppScreenState

@PreviewCompactScreen
@Composable
private fun AppScreenCompactScreenPreviews(
    @PreviewParameter(AppScreenStateCompactPreviewProvider::class) previewModel: PreviewModel<AppScreenState>,
) {
    AppScreenPreview(previewModel = previewModel)
}

private class AppScreenStateCompactPreviewProvider : PreviewParameterProvider<PreviewModel<AppScreenState>> {
    override val values: Sequence<PreviewModel<AppScreenState>> =
        AppScreenPreviewModels().values.filter { previewModel -> previewModel.isCompact }
}

@PreviewLargeScreen
@Composable
private fun AppScreenLargeScreenPreviews(
    @PreviewParameter(AppScreenStateLargePreviewProvider::class) previewModel: PreviewModel<AppScreenState>,
) {
    AppScreenPreview(previewModel = previewModel)
}

private class AppScreenStateLargePreviewProvider : PreviewParameterProvider<PreviewModel<AppScreenState>> {
    override val values: Sequence<PreviewModel<AppScreenState>> =
        AppScreenPreviewModels().values.filter { previewModel -> !previewModel.isCompact }
}
