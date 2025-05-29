@file:Suppress("UnusedPrivateMember")

package theoneclick.client.app.ui.previews.screens.homeScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.app.ui.previews.annotations.PreviewScreens
import theoneclick.client.app.ui.previews.providers.base.PreviewModel
import theoneclick.client.app.ui.previews.providers.screens.homeScreen.UserSettingsScreenPreviewModels
import theoneclick.client.app.ui.screens.homeScreen.UserSettingsPreview
import theoneclick.client.app.ui.states.homeScreen.UserSettingsState

@PreviewScreens
@Composable
private fun UserSettingsPreviews(
    @PreviewParameter(UserSettingsScreenPreviewProvider::class) previewModel: PreviewModel<UserSettingsState>,
) {
    UserSettingsPreview(previewModel)
}

private class UserSettingsScreenPreviewProvider : PreviewParameterProvider<PreviewModel<UserSettingsState>> {
    override val values: Sequence<PreviewModel<UserSettingsState>> = UserSettingsScreenPreviewModels().values
}
