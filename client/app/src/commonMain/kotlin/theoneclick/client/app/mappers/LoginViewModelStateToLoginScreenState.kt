package theoneclick.client.app.mappers

import theoneclick.client.app.ui.screens.LoginScreenState
import theoneclick.client.app.viewModels.LoginViewModel
import theoneclick.client.shared.ui.models.Field
import theoneclick.shared.core.validators.passwordValidator
import theoneclick.shared.core.validators.usernameValidator

fun LoginViewModel.LoginViewModelState.toLoginScreenState(): LoginScreenState {
    val isUsernameValid = usernameValidator.isValid(username)
    val isPasswordValid = passwordValidator.isValid(password)

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