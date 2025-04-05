package theoneclick.client.core.viewModels.homeScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import theoneclick.client.core.models.results.LogoutResult
import theoneclick.client.core.platform.AuthenticationDataSource
import theoneclick.client.core.ui.events.homeScreen.UserSettingsEvent
import theoneclick.client.core.ui.states.homeScreen.UserSettingsState

class UserSettingsViewModel(
    private val authenticationDataSource: AuthenticationDataSource,
) : ViewModel() {
    private val _state = mutableStateOf(UserSettingsState())
    val state: State<UserSettingsState> = _state

    private var logoutJob: Job? = null

    fun onEvent(event: UserSettingsEvent) {
        when (event) {
            is UserSettingsEvent.LogoutClicked -> event.handleLogoutClicked()
            is UserSettingsEvent.SuccessShown -> event.handleSuccessShown()
            is UserSettingsEvent.ErrorShown -> event.handleErrorShown()
        }
    }

    private fun UserSettingsEvent.LogoutClicked.handleLogoutClicked() {
        logoutJob?.cancel()

        logoutJob = viewModelScope.launch {
            authenticationDataSource
                .logout()
                .onStart {
                    _state.value = _state.value.copy(
                        isLoading = true,
                        isButtonEnabled = false,
                        showError = false,
                        showSuccess = false,
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
                        is LogoutResult.Success ->
                            _state.value = _state.value.copy(showSuccess = true)

                        is LogoutResult.Failure ->
                            _state.value = _state.value.copy(showError = true)
                    }
                }
        }
    }

    private fun UserSettingsEvent.SuccessShown.handleSuccessShown() {
        _state.value = _state.value.copy(showSuccess = false)
    }

    private fun UserSettingsEvent.ErrorShown.handleErrorShown() {
        _state.value = _state.value.copy(showError = false)
    }

    override fun onCleared() {
        super.onCleared()

        logoutJob?.cancel()
    }
}
