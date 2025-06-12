package theoneclick.client.app.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.getString
import theoneclick.client.app.generated.resources.Res
import theoneclick.client.app.generated.resources.loginScreen_snackbar_unknownError
import theoneclick.client.app.mappers.toLoginScreenState
import theoneclick.client.app.ui.events.LoginEvent
import theoneclick.client.app.ui.screens.LoginScreenState
import theoneclick.client.shared.navigation.NavigationController
import theoneclick.client.shared.navigation.NavigationController.NavigationEvent.Navigate
import theoneclick.client.shared.navigation.popUpToInclusive
import theoneclick.client.shared.network.models.RequestLoginResult
import theoneclick.client.shared.network.platform.AuthenticationDataSource
import theoneclick.client.shared.notifications.NotificationsController
import theoneclick.shared.core.models.routes.AppRoute

@Inject
class LoginViewModel(
    private val navigationController: NavigationController,
    private val authenticationDataSource: AuthenticationDataSource,
    private val notificationsController: NotificationsController,
) : ViewModel() {

    private val loginState = MutableStateFlow<LoginState>(LoginState())
    val loginScreenState: StateFlow<LoginScreenState> =
        loginState
            .map(LoginState::toLoginScreenState)
            .stateIn(viewModelScope, SharingStarted.Eagerly, LoginScreenState())

    private var requestLoginJob: Job? = null

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UsernameChanged -> event.handleUsernameChange()
            is LoginEvent.PasswordChanged -> event.handlePasswordChange()

            is LoginEvent.RegisterButtonClicked -> handleRegisterButtonClicked()
        }
    }

    private fun LoginEvent.UsernameChanged.handleUsernameChange() {
        loginState.value = loginState.value.copy(
            username = newUsername,
        )
    }

    private fun LoginEvent.PasswordChanged.handlePasswordChange() {
        loginState.value = loginState.value.copy(
            password = newPassword,
        )
    }

    private fun handleRegisterButtonClicked() {
        requestLoginJob?.cancel()

        requestLoginJob = viewModelScope.launch {
            authenticationDataSource
                .login(
                    username = loginState.value.username!!,
                    password = loginState.value.password!!,
                )
                .onStart {
                    loginState.value = loginState.value.copy(isLoading = true)
                }
                .onCompletion {
                    loginState.value = loginState.value.copy(isLoading = false)
                }
                .collect { result ->
                    when (result) {
                        is RequestLoginResult.ValidLogin -> navigateToHome()

                        is RequestLoginResult.Error -> {
                            notificationsController.showErrorNotification(
                                getString(Res.string.loginScreen_snackbar_unknownError)
                            )
                        }
                    }
                }
        }
    }

    private suspend fun navigateToHome() {
        navigationController.sendNavigationEvent(
            navigationEvent = Navigate(
                destinationRoute = AppRoute.Home,
                launchSingleTop = true,
                popUpTo = popUpToInclusive(startRoute = AppRoute.Login),
            )
        )
    }

    override fun onCleared() {
        super.onCleared()

        requestLoginJob?.cancel()
    }

    data class LoginState(
        val username: String? = null,
        val password: String? = null,
        val isLoading: Boolean = false,
    )
}
