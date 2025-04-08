@file:Suppress("UnusedPrivateMember")

package theoneclick.client.core.ui.previews.screens.homeScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.core.ui.previews.annotations.PreviewScreens
import theoneclick.client.core.ui.previews.providers.base.PreviewModel
import theoneclick.client.core.ui.previews.providers.screens.homeScreen.DevicesListScreenPreviewModels
import theoneclick.client.core.ui.screens.homeScreen.DevicesListScreenPreview
import theoneclick.client.core.ui.states.homeScreen.DevicesListState

@PreviewScreens
@Composable
private fun DevicesListScreenPreviews(
    @PreviewParameter(DevicesListScreenPreviewProvider::class) previewModel: PreviewModel<DevicesListState>,
) {
    DevicesListScreenPreview(previewModel)
}

private class DevicesListScreenPreviewProvider : PreviewParameterProvider<PreviewModel<DevicesListState>> {
    override val values: Sequence<PreviewModel<DevicesListState>> = DevicesListScreenPreviewModels().values
}
