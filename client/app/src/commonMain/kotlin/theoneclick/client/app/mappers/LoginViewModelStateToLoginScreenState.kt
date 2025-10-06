package oneclick.client.app.mappers

import oneclick.client.app.ui.screens.LoginScreenState
import oneclick.client.app.viewModels.LoginViewModel
import oneclick.client.shared.ui.models.Field
import oneclick.shared.contracts.auth.models.Password
import oneclick.shared.contracts.auth.models.Username

fun LoginViewModel.LoginViewModelState.toLoginScreenState(): LoginScreenState {
    val isUsernameValid = Username.isValid(username ?: "")
    val isPasswordValid = Password.isValid(password ?: "")

    return LoginScreenState(
        username = Field(
            text = username ?: "",
            isValid = isUsernameValid.takeIf { username != null },
        ),
        password = Field(
            text = password ?: "",
            isValid = isPasswordValid.takeIf { password != null },
        ),
        isRegisterButtonEnabled = isUsernameValid && isPasswordValid && !isLoading,
        isLoading = isLoading
    )
}
