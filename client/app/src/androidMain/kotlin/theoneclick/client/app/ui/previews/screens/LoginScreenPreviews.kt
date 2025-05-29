@file:Suppress("UnusedPrivateMember")

package theoneclick.client.app.ui.previews.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.app.ui.previews.annotations.PreviewScreens
import theoneclick.client.app.ui.previews.providers.base.PreviewModel
import theoneclick.client.app.ui.previews.providers.screens.LoginScreenPreviewModels
import theoneclick.client.app.ui.screens.LoginScreenPreview
import theoneclick.client.app.ui.states.LoginState

@PreviewScreens
@Composable
private fun LoginScreenPreviews(
    @PreviewParameter(LoginScreenPreviewProvider::class) previewModel: PreviewModel<LoginState>,
) {
    LoginScreenPreview(previewModel)
}

private class LoginScreenPreviewProvider : PreviewParameterProvider<PreviewModel<LoginState>> {
    override val values: Sequence<PreviewModel<LoginState>> = LoginScreenPreviewModels().values
}
