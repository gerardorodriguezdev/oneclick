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
import theoneclick.client.app.ui.screens.LoginEvent
import theoneclick.client.app.ui.screens.LoginScreenState
import theoneclick.client.shared.navigation.NavigationController
import theoneclick.client.shared.navigation.NavigationController.NavigationEvent.Navigate
import theoneclick.client.shared.navigation.models.routes.AppRoute
import theoneclick.client.shared.navigation.popUpToInclusive
import theoneclick.client.shared.network.models.RequestLoginResult
import theoneclick.client.shared.network.platform.AuthenticationDataSource
import theoneclick.client.shared.notifications.NotificationsController
import theoneclick.shared.contracts.core.dtos.PasswordDto.Companion.toPassword
import theoneclick.shared.contracts.core.dtos.UsernameDto.Companion.toUsername
import theoneclick.shared.contracts.core.dtos.requests.RequestLoginRequestDto

@Inject
class LoginViewModel(
    private val navigationController: NavigationController,
    private val authenticationDataSource: AuthenticationDataSource,
    private val notificationsController: NotificationsController,
) : ViewModel() {

    private val loginViewModelState = MutableStateFlow(LoginViewModelState())
    val loginScreenState: StateFlow<LoginScreenState> =
        loginViewModelState
            .map(LoginViewModelState::toLoginScreenState)
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
        loginViewModelState.value = loginViewModelState.value.copy(
            username = newUsername,
        )
    }

    private fun LoginEvent.PasswordChanged.handlePasswordChange() {
        loginViewModelState.value = loginViewModelState.value.copy(
            password = newPassword,
        )
    }

    private fun handleRegisterButtonClicked() {
        requestLoginJob?.cancel()

        requestLoginJob = viewModelScope.launch {
            authenticationDataSource
                .login(
                    request = RequestLoginRequestDto(
                        username = requireNotNull(loginViewModelState.value.username?.toUsername()),
                        password = requireNotNull(loginViewModelState.value.password?.toPassword()),
                    )
                )
                .onStart {
                    loginViewModelState.value = loginViewModelState.value.copy(isLoading = true)
                }
                .onCompletion {
                    loginViewModelState.value = loginViewModelState.value.copy(isLoading = false)
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

    data class LoginViewModelState(
        val username: String? = null,
        val password: String? = null,
        val isLoading: Boolean = false,
    )
}
