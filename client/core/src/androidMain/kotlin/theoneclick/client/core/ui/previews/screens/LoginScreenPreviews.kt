@file:Suppress("UnusedPrivateMember")

package theoneclick.client.core.ui.previews.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.core.ui.previews.annotations.PreviewScreens
import theoneclick.client.core.ui.previews.providers.base.PreviewModel
import theoneclick.client.core.ui.previews.providers.screens.LoginScreenPreviewModels
import theoneclick.client.core.ui.screens.LoginScreenPreview
import theoneclick.client.core.ui.states.LoginState

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
