package oneclick.client.apps.user.core.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import oneclick.client.apps.user.core.generated.resources.Res
import oneclick.client.apps.user.core.generated.resources.loginScreen_snackbar_unknownError
import oneclick.client.apps.user.core.generated.resources.loginScreen_snackbar_waitForApproval
import oneclick.client.apps.user.core.mappers.toLoginScreenState
import oneclick.client.apps.user.core.ui.screens.LoginEvent
import oneclick.client.apps.user.core.ui.screens.LoginScreenState
import oneclick.client.apps.user.navigation.NavigationController
import oneclick.client.apps.user.navigation.NavigationController.NavigationEvent.Navigate
import oneclick.client.apps.user.navigation.models.routes.AppRoute
import oneclick.client.apps.user.navigation.popUpToInclusive
import oneclick.client.apps.user.notifications.NotificationsController
import oneclick.client.shared.network.models.RequestLoginResult
import oneclick.client.shared.network.platform.AuthenticationDataSource
import oneclick.shared.contracts.auth.models.Password.Companion.toPassword
import oneclick.shared.contracts.auth.models.Username.Companion.toUsername
import oneclick.shared.contracts.auth.models.requests.LoginRequest.UserRequestLoginRequest
import org.jetbrains.compose.resources.getString

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
            loginViewModelState.value = loginViewModelState.value.copy(isLoading = true)

            val requestLoginResult = authenticationDataSource
                .login(
                    request = UserRequestLoginRequest(
                        username = requireNotNull(loginViewModelState.value.username?.toUsername()),
                        password = requireNotNull(loginViewModelState.value.password?.toPassword()),
                    )
                )

            when (requestLoginResult) {
                is RequestLoginResult.ValidLogin -> navigateToHome()

                is RequestLoginResult.WaitForApproval -> {
                    notificationsController.showSuccessNotification(
                        getString(Res.string.loginScreen_snackbar_waitForApproval)
                    )
                }

                is RequestLoginResult.Error -> {
                    notificationsController.showErrorNotification(
                        getString(Res.string.loginScreen_snackbar_unknownError)
                    )
                }
            }

            loginViewModelState.value = loginViewModelState.value.copy(isLoading = false)
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
