package theoneclick.client.core.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import theoneclick.client.core.platform.AuthenticationDataSource
import theoneclick.client.core.extensions.popUpToInclusive
import theoneclick.client.core.models.results.RequestLoginResult
import theoneclick.client.core.navigation.NavigationController
import theoneclick.client.core.navigation.NavigationController.NavigationEvent.Navigate
import theoneclick.client.core.ui.events.LoginEvent
import theoneclick.client.core.ui.states.LoginState
import theoneclick.shared.core.models.routes.AppRoute
import theoneclick.shared.core.validators.passwordValidator
import theoneclick.shared.core.validators.usernameValidator

class LoginViewModel(
    private val navigationController: NavigationController,
    private val authenticationDataSource: AuthenticationDataSource,
) : ViewModel() {

    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state

    private var requestLoginJob: Job? = null

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UsernameChanged -> event.handleUsernameChange()
            is LoginEvent.PasswordChanged -> event.handlePasswordChange()

            is LoginEvent.RegisterButtonClicked -> event.handleRegisterButtonClicked()

            is LoginEvent.ErrorShown -> event.handleErrorShown()
        }
    }

    private fun LoginEvent.UsernameChanged.handleUsernameChange() {
        val isNewUsernameValid = usernameValidator.isValid(newUsername)

        _state.value = _state.value.copy(
            username = newUsername,
            isUsernameValid = isNewUsernameValid,
            isRegisterButtonEnabled = isNewUsernameValid && passwordValidator.isValid(_state.value.password),
        )
    }

    private fun LoginEvent.PasswordChanged.handlePasswordChange() {
        val isNewPasswordValid = passwordValidator.isValid(newPassword)

        _state.value = _state.value.copy(
            password = newPassword,
            isPasswordValid = isNewPasswordValid,
            isRegisterButtonEnabled = isNewPasswordValid && usernameValidator.isValid(_state.value.username),
        )
    }

    private fun LoginEvent.RegisterButtonClicked.handleRegisterButtonClicked() {
        requestLoginJob?.cancel()

        requestLoginJob = viewModelScope.launch {
            authenticationDataSource
                .login(
                    username = _state.value.username,
                    password = _state.value.password,
                )
                .onStart {
                    _state.value = _state.value.copy(
                        isLoading = true,
                        isRegisterButtonEnabled = false,
                        showError = false,
                    )
                }
                .onCompletion {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isRegisterButtonEnabled = true,
                    )
                }
                .collect { result ->
                    when (result) {
                        is RequestLoginResult.ValidLogin ->
                            navigationController.sendNavigationEvent(navigationEvent = result.toNavigationEvent())

                        is RequestLoginResult.Failure ->
                            _state.value = _state.value.copy(showError = true)
                    }
                }
        }
    }

    private fun RequestLoginResult.ValidLogin.toNavigationEvent(): NavigationController.NavigationEvent =
        Navigate(
            destinationRoute = AppRoute.Home,
            launchSingleTop = true,
            popUpTo = popUpToInclusive(startRoute = AppRoute.Login),
        )

    private fun LoginEvent.ErrorShown.handleErrorShown() {
        _state.value = _state.value.copy(showError = false)
    }

    override fun onCleared() {
        super.onCleared()

        requestLoginJob?.cancel()
    }
}
