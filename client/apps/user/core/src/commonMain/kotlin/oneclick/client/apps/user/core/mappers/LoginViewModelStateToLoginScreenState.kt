package oneclick.client.apps.user.core.mappers

import oneclick.client.apps.user.core.ui.screens.LoginScreenState
import oneclick.client.apps.user.core.viewModels.LoginViewModel
import oneclick.shared.contracts.auth.models.Password
import oneclick.shared.contracts.auth.models.Username

fun LoginViewModel.LoginViewModelState.toLoginScreenState(): LoginScreenState {
    val isUsernameValid = Username.isValid(username ?: "")
    val isPasswordValid = Password.isValid(password ?: "")

    return LoginScreenState(
        username = _root_ide_package_.oneclick.client.apps.user.ui.models.Field(
            text = username ?: "",
            isValid = isUsernameValid.takeIf { username != null },
        ),
        password = _root_ide_package_.oneclick.client.apps.user.ui.models.Field(
            text = password ?: "",
            isValid = isPasswordValid.takeIf { password != null },
        ),
        isRegisterButtonEnabled = isUsernameValid && isPasswordValid && !isLoading,
        isLoading = isLoading
    )
}
