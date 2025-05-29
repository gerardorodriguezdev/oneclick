package theoneclick.client.app.ui.previews.providers.screens

import theoneclick.client.app.ui.previews.providers.base.PreviewModel
import theoneclick.client.app.ui.previews.providers.base.PreviewModelProvider
import theoneclick.client.app.ui.previews.providers.base.darkThemeCompactPreviewModel
import theoneclick.client.app.ui.previews.providers.base.lightThemeCompactPreviewModel
import theoneclick.client.app.ui.states.LoginState

class LoginScreenPreviewModels : PreviewModelProvider<LoginState> {

    override val values: Sequence<PreviewModel<LoginState>> =
        sequenceOf(
            // Light
            lightThemeCompactPreviewModel(description = "Initial", initialState),
            lightThemeCompactPreviewModel(description = "InvalidUsername", invalidUsernameState),
            lightThemeCompactPreviewModel(description = "InvalidPassword", invalidPasswordState),
            lightThemeCompactPreviewModel(description = "Loading", loadingState),
            lightThemeCompactPreviewModel(description = "Error", errorState),
            lightThemeCompactPreviewModel(description = "Valid", validState),

            // Dark
            darkThemeCompactPreviewModel(description = "Initial", initialState),
            darkThemeCompactPreviewModel(description = "InvalidUsername", invalidUsernameState),
            darkThemeCompactPreviewModel(description = "InvalidPassword", invalidPasswordState),
            darkThemeCompactPreviewModel(description = "Loading", loadingState),
            darkThemeCompactPreviewModel(description = "Error", errorState),
            darkThemeCompactPreviewModel(description = "Valid", validState),
        )

    companion object {
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

        val errorState = LoginState(
            username = USERNAME,
            isUsernameValid = true,
            password = PASSWORD,
            isPasswordValid = true,
            isRegisterButtonEnabled = true,
            showError = true,
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
