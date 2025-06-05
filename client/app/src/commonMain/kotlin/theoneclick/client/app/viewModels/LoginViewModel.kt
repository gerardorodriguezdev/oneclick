package theoneclick.client.app.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import theoneclick.client.app.ui.events.LoginEvent
import theoneclick.client.app.ui.states.LoginState
import theoneclick.client.shared.navigation.NavigationController
import theoneclick.client.shared.navigation.NavigationController.NavigationEvent.Navigate
import theoneclick.client.shared.navigation.popUpToInclusive
import theoneclick.client.shared.network.models.RequestLoginResult
import theoneclick.client.shared.network.platform.AuthenticationDataSource
import theoneclick.shared.core.models.routes.AppRoute
import theoneclick.shared.core.validators.passwordValidator
import theoneclick.shared.core.validators.usernameValidator

@Inject
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

            is LoginEvent.RegisterButtonClicked -> handleRegisterButtonClicked()

            is LoginEvent.ErrorShown -> handleErrorShown()
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

    private fun handleRegisterButtonClicked() {
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
                            navigationController.sendNavigationEvent(navigationEvent = toNavigationEvent())

                        is RequestLoginResult.Failure ->
                            _state.value = _state.value.copy(showError = true)
                    }
                }
        }
    }

    private fun toNavigationEvent(): NavigationController.NavigationEvent =
        Navigate(
            destinationRoute = AppRoute.Home,
            launchSingleTop = true,
            popUpTo = popUpToInclusive(startRoute = AppRoute.Login),
        )

    private fun handleErrorShown() {
        _state.value = _state.value.copy(showError = false)
    }

    override fun onCleared() {
        super.onCleared()

        requestLoginJob?.cancel()
    }
}
