package theoneclick.client.features.home.viewModels

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
import theoneclick.client.features.home.generated.resources.Res
import theoneclick.client.features.home.generated.resources.userSettingsScreen_snackbar_logout
import theoneclick.client.features.home.generated.resources.userSettingsScreen_snackbar_unknownError
import theoneclick.client.features.home.states.UserSettingsState
import theoneclick.client.features.home.ui.events.UserSettingsEvent
import theoneclick.client.shared.network.models.LogoutResult
import theoneclick.client.shared.network.platform.AuthenticationDataSource
import theoneclick.client.shared.notifications.NotificationsController

@Inject
internal class UserSettingsViewModel(
    private val authenticationDataSource: AuthenticationDataSource,
    private val notificationsController: NotificationsController,
) : ViewModel() {
    private val _state = mutableStateOf(UserSettingsState())
    val state: State<UserSettingsState> = _state

    private var logoutJob: Job? = null

    fun onEvent(event: UserSettingsEvent) {
        when (event) {
            is UserSettingsEvent.LogoutClicked -> handleLogoutClicked()
        }
    }

    private fun handleLogoutClicked() {
        logoutJob?.cancel()

        logoutJob = viewModelScope.launch {
            authenticationDataSource
                .logout()
                .onStart {
                    _state.value = _state.value.copy(
                        isLoading = true,
                        isButtonEnabled = false,
                    )
                }
                .onCompletion {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isButtonEnabled = true,
                    )
                }
                .collect { logoutResult ->
                    when (logoutResult) {
                        is LogoutResult.Success -> {
                            notificationsController.showSuccessNotification(
                                getString(Res.string.userSettingsScreen_snackbar_logout),
                            )
                        }

                        is LogoutResult.Error -> {
                            notificationsController.showErrorNotification(
                                getString(Res.string.userSettingsScreen_snackbar_unknownError),
                            )
                        }
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()

        logoutJob?.cancel()
    }
}
