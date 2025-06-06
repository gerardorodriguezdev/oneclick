package theoneclick.client.app.ui.previews.providers.screens

import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import theoneclick.client.app.ui.states.LoginState
import theoneclick.client.shared.ui.previews.providers.base.PreviewModel
import theoneclick.client.shared.ui.previews.providers.base.darkThemeCompactPreviewModel
import theoneclick.client.shared.ui.previews.providers.base.lightThemeCompactPreviewModel

class LoginStateProvider : PreviewParameterProvider<PreviewModel<LoginState>> {

    override val values: Sequence<PreviewModel<LoginState>> =
        sequenceOf(
            // Light
            lightThemeCompactPreviewModel(description = "Initial", initialState),
            lightThemeCompactPreviewModel(description = "InvalidUsername", invalidUsernameState),
            lightThemeCompactPreviewModel(description = "InvalidPassword", invalidPasswordState),
            lightThemeCompactPreviewModel(description = "Loading", loadingState),
            lightThemeCompactPreviewModel(description = "Valid", validState),

            // Dark
            darkThemeCompactPreviewModel(description = "Initial", initialState),
            darkThemeCompactPreviewModel(description = "InvalidUsername", invalidUsernameState),
            darkThemeCompactPreviewModel(description = "InvalidPassword", invalidPasswordState),
            darkThemeCompactPreviewModel(description = "Loading", loadingState),
            darkThemeCompactPreviewModel(description = "Valid", validState),
        )

    companion object Companion {
        const val USERNAME = "Username"
        const val PASSWORD = "Password"

        val initialState = LoginState()

        val invalidUsernameState = LoginState(
            username = USERNAME,
            isUsernameValid = false,
            password = PASSWORD,
            isPasswordValid = true,
            isRegisterButtonEnabled = false,
        )

        val invalidPasswordState = LoginState(
            username = USERNAME,
            isUsernameValid = true,
            password = PASSWORD,
            isPasswordValid = false,
            isRegisterButtonEnabled = false,
        )

        val loadingState = LoginState(
            username = USERNAME,
            isUsernameValid = true,
            password = PASSWORD,
            isPasswordValid = true,
            isRegisterButtonEnabled = false,
            isLoading = true,
        )

        val validState = LoginState(
            username = USERNAME,
            isUsernameValid = true,
            password = PASSWORD,
            isPasswordValid = true,
            isRegisterButtonEnabled = true,
        )
    }
}
