@file:Suppress("UnusedPrivateMember")

package theoneclick.client.core.ui.previews.screens.homeScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.core.ui.previews.annotations.PreviewScreens
import theoneclick.client.core.ui.previews.providers.base.PreviewModel
import theoneclick.client.core.ui.previews.providers.screens.homeScreen.UserSettingsScreenPreviewModels
import theoneclick.client.core.ui.screens.homeScreen.UserSettingsPreview
import theoneclick.client.core.ui.states.homeScreen.UserSettingsState

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
