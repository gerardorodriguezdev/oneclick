@file:Suppress("UnusedPrivateMember")

package theoneclick.client.core.ui.previews.screens.homeScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.core.ui.previews.annotations.PreviewScreens
import theoneclick.client.core.ui.previews.providers.base.PreviewModel
import theoneclick.client.core.ui.previews.providers.screens.homeScreen.AddDeviceScreenPreviewModels
import theoneclick.client.core.ui.screens.homeScreen.AddDeviceScreenPreview
import theoneclick.client.core.ui.states.homeScreen.AddDeviceState

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
