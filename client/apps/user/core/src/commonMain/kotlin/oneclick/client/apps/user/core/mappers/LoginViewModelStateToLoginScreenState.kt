package oneclick.client.apps.user.core.mappers

import oneclick.client.apps.user.core.ui.screens.LoginScreenState
import oneclick.client.apps.user.core.viewModels.LoginViewModel
import oneclick.client.apps.user.ui.models.Field
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
