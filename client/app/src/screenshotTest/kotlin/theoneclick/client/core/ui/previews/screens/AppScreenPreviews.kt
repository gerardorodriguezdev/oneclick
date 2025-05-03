@file:Suppress("UnusedPrivateMember")

package theoneclick.client.core.ui.previews.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.core.ui.previews.annotations.PreviewCompactScreen
import theoneclick.client.core.ui.previews.annotations.PreviewLargeScreen
import theoneclick.client.core.ui.previews.providers.base.PreviewModel
import theoneclick.client.core.ui.previews.providers.screens.AppScreenPreviewModels
import theoneclick.client.core.ui.screens.AppScreenPreview
import theoneclick.client.core.ui.screens.AppScreenState

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
