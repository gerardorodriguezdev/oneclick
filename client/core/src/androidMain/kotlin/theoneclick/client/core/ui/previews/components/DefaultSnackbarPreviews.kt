@file:Suppress("UnusedPrivateMember")

package theoneclick.client.core.ui.previews.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.core.ui.components.DefaultSnackbarPreview
import theoneclick.client.core.ui.previews.annotations.PreviewComponents
import theoneclick.client.core.ui.previews.providers.base.PreviewModel
import theoneclick.client.core.ui.previews.providers.components.DefaultSnackbarPreviewModels
import theoneclick.client.core.ui.previews.providers.components.DefaultSnackbarPreviewModels.DefaultSnackbarModel

@PreviewComponents
@Composable
private fun DefaultSnackbarPreviews(
    @PreviewParameter(DefaultSnackbarPreviewProvider::class) previewModel: PreviewModel<DefaultSnackbarModel>,
) {
    DefaultSnackbarPreview(previewModel)
}

private class DefaultSnackbarPreviewProvider : PreviewParameterProvider<PreviewModel<DefaultSnackbarModel>> {
    override val values: Sequence<PreviewModel<DefaultSnackbarModel>> = DefaultSnackbarPreviewModels().values
}
