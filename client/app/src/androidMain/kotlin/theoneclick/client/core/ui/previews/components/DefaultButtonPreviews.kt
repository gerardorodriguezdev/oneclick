@file:Suppress("UnusedPrivateMember")

package theoneclick.client.core.ui.previews.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.core.ui.components.DefaultButtonPreview
import theoneclick.client.core.ui.previews.annotations.PreviewComponents
import theoneclick.client.core.ui.previews.providers.base.PreviewModel
import theoneclick.client.core.ui.previews.providers.components.DefaultButtonPreviewModels
import theoneclick.client.core.ui.previews.providers.components.DefaultButtonPreviewModels.DefaultButtonModel

@PreviewComponents
@Composable
private fun DefaultButtonPreviews(
    @PreviewParameter(DefaultButtonPreviewProvider::class) previewModel: PreviewModel<DefaultButtonModel>,
) {
    DefaultButtonPreview(previewModel)
}

private class DefaultButtonPreviewProvider : PreviewParameterProvider<PreviewModel<DefaultButtonModel>> {
    override val values: Sequence<PreviewModel<DefaultButtonModel>> = DefaultButtonPreviewModels().values
}
