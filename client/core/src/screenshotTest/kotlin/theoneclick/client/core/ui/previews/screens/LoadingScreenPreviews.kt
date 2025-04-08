@file:Suppress("UnusedPrivateMember")

package theoneclick.client.core.ui.previews.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.core.ui.previews.annotations.PreviewScreens
import theoneclick.client.core.ui.previews.providers.base.PreviewModel
import theoneclick.client.core.ui.previews.providers.screens.LoadingScreenPreviewModels
import theoneclick.client.core.ui.screens.LoadingScreenPreview

@PreviewScreens
@Composable
private fun LoadingScreenPreviews(
    @PreviewParameter(LoadingScreenPreviewProvider::class) previewModel: PreviewModel<Unit>,
) {
    LoadingScreenPreview(previewModel)
}

private class LoadingScreenPreviewProvider : PreviewParameterProvider<PreviewModel<Unit>> {
    override val values: Sequence<PreviewModel<Unit>> = LoadingScreenPreviewModels().values
}
