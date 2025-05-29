@file:Suppress("UnusedPrivateMember")

package theoneclick.client.app.ui.previews.screens.homeScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.app.ui.previews.annotations.PreviewScreens
import theoneclick.client.app.ui.previews.providers.base.PreviewModel
import theoneclick.client.app.ui.previews.providers.screens.homeScreen.DevicesListScreenPreviewModels
import theoneclick.client.app.ui.screens.homeScreen.DevicesListScreenPreview
import theoneclick.client.app.ui.states.homeScreen.DevicesListState

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
