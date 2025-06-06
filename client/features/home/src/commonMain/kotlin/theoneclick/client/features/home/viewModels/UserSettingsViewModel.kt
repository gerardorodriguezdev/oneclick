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
import theoneclick.client.features.home.states.UserSettingsState
import theoneclick.client.features.home.ui.events.UserSettingsEvent
import theoneclick.client.shared.network.models.LogoutResult
import theoneclick.client.shared.network.platform.AuthenticationDataSource

@Inject
internal class UserSettingsViewModel(
    private val authenticationDataSource: AuthenticationDataSource,
) : ViewModel() {
    private val _state = mutableStateOf(UserSettingsState())
    val state: State<UserSettingsState> = _state

    private var logoutJob: Job? = null

    fun onEvent(event: UserSettingsEvent) {
        when (event) {
            is UserSettingsEvent.LogoutClicked -> handleLogoutClicked()
            is UserSettingsEvent.SuccessShown -> handleSuccessShown()
            is UserSettingsEvent.ErrorShown -> handleErrorShown()
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

                        is LogoutResult.Error ->
                            _state.value = _state.value.copy(showError = true)
                    }
                }
        }
    }

    private fun handleSuccessShown() {
        _state.value = _state.value.copy(showSuccess = false)
    }

    private fun handleErrorShown() {
        _state.value = _state.value.copy(showError = false)
    }

    override fun onCleared() {
        super.onCleared()

        logoutJob?.cancel()
    }
}
