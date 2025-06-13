package theoneclick.client.app.mappers

import theoneclick.client.app.ui.screens.LoginScreenState
import theoneclick.client.app.viewModels.LoginViewModel
import theoneclick.client.shared.ui.models.Field

fun LoginViewModel.LoginViewModelState.toLoginScreenState(): LoginScreenState {
    val isUsernameValid = true //TODO: Fix
    val isPasswordValid = true //TODO: Fix

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