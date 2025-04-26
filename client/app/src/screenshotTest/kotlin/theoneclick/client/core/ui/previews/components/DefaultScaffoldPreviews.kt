@file:Suppress("UnusedPrivateMember")

package theoneclick.client.core.ui.previews.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.core.ui.components.DefaultScaffoldPreview
import theoneclick.client.core.ui.components.SnackbarState
import theoneclick.client.core.ui.previews.annotations.PreviewScreens
import theoneclick.client.core.ui.previews.providers.base.PreviewModel
import theoneclick.client.core.ui.previews.providers.components.DefaultScaffoldPreviewModels

@PreviewScreens
@Composable
private fun DefaultScaffoldPreviews(
    @PreviewParameter(DefaultScaffoldPreviewProvider::class) previewModel: PreviewModel<SnackbarState>,
) {
    DefaultScaffoldPreview(previewModel)
}

private class DefaultScaffoldPreviewProvider : PreviewParameterProvider<PreviewModel<SnackbarState>> {
    override val values: Sequence<PreviewModel<SnackbarState>> = DefaultScaffoldPreviewModels().values
}
