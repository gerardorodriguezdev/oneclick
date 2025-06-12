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
import org.jetbrains.compose.resources.getString
import theoneclick.client.app.generated.resources.Res
import theoneclick.client.app.generated.resources.loginScreen_snackbar_unknownError
import theoneclick.client.app.ui.events.LoginEvent
import theoneclick.client.app.ui.states.LoginState
import theoneclick.client.shared.navigation.NavigationController
import theoneclick.client.shared.navigation.NavigationController.NavigationEvent.Navigate
import theoneclick.client.shared.navigation.popUpToInclusive
import theoneclick.client.shared.network.models.RequestLoginResult
import theoneclick.client.shared.network.platform.AuthenticationDataSource
import theoneclick.client.shared.notifications.NotificationsController
import theoneclick.client.shared.ui.models.Field
import theoneclick.shared.core.models.routes.AppRoute
import theoneclick.shared.core.validators.passwordValidator
import theoneclick.shared.core.validators.usernameValidator

@Inject
class LoginViewModel(
    private val navigationController: NavigationController,
    private val authenticationDataSource: AuthenticationDataSource,
    private val notificationsController: NotificationsController,
) : ViewModel() {

    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state

    private var requestLoginJob: Job? = null

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UsernameChanged -> event.handleUsernameChange()
            is LoginEvent.PasswordChanged -> event.handlePasswordChange()

            is LoginEvent.RegisterButtonClicked -> handleRegisterButtonClicked()
        }
    }

    private fun LoginEvent.UsernameChanged.handleUsernameChange() {
        val isNewUsernameValid = usernameValidator.isValid(newUsername)

        _state.value = _state.value.copy(
            username = Field(
                text = newUsername,
                isValid = isNewUsernameValid,
            ),
            isRegisterButtonEnabled = isNewUsernameValid && passwordValidator.isValid(_state.value.password.text),
        )
    }

    private fun LoginEvent.PasswordChanged.handlePasswordChange() {
        val isNewPasswordValid = passwordValidator.isValid(newPassword)

        _state.value = _state.value.copy(
            password = Field(
                text = newPassword,
                isValid = isNewPasswordValid,
            ),
            isRegisterButtonEnabled = isNewPasswordValid && usernameValidator.isValid(_state.value.username.text),
        )
    }

    private fun handleRegisterButtonClicked() {
        requestLoginJob?.cancel()

        requestLoginJob = viewModelScope.launch {
            authenticationDataSource
                .login(
                    username = _state.value.username.text,
                    password = _state.value.password.text,
                )
                .onStart {
                    _state.value = _state.value.copy(
                        isLoading = true,
                        isRegisterButtonEnabled = false,
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

                        is RequestLoginResult.Error -> {
                            notificationsController.showErrorNotification(
                                getString(Res.string.loginScreen_snackbar_unknownError)
                            )
                        }
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

    override fun onCleared() {
        super.onCleared()

        requestLoginJob?.cancel()
    }
}
