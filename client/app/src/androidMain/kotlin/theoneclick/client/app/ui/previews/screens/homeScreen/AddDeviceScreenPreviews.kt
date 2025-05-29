@file:Suppress("UnusedPrivateMember")

package theoneclick.client.app.ui.previews.screens.homeScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.app.ui.previews.annotations.PreviewScreens
import theoneclick.client.app.ui.previews.providers.base.PreviewModel
import theoneclick.client.app.ui.previews.providers.screens.homeScreen.AddDeviceScreenPreviewModels
import theoneclick.client.app.ui.screens.homeScreen.AddDeviceScreenPreview
import theoneclick.client.app.ui.states.homeScreen.AddDeviceState

@PreviewScreens
@Composable
private fun AddDeviceScreenPreviews(
    @PreviewParameter(AddDeviceScreenPreviewProvider::class) previewModel: PreviewModel<AddDeviceState>,
) {
    AddDeviceScreenPreview(previewModel)
}

private class AddDeviceScreenPreviewProvider : PreviewParameterProvider<PreviewModel<AddDeviceState>> {
    override val values: Sequence<PreviewModel<AddDeviceState>> = AddDeviceScreenPreviewModels().values
}
