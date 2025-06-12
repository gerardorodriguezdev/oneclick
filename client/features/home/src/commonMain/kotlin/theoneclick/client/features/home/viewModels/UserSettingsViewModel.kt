package theoneclick.client.features.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.getString
import theoneclick.client.features.home.generated.resources.Res
import theoneclick.client.features.home.generated.resources.userSettingsScreen_snackbar_logout
import theoneclick.client.features.home.generated.resources.userSettingsScreen_snackbar_unknownError
import theoneclick.client.features.home.mapper.toUserSettingsScreenState
import theoneclick.client.features.home.ui.events.UserSettingsEvent
import theoneclick.client.features.home.ui.screens.UserSettingsScreenState
import theoneclick.client.shared.network.models.LogoutResult
import theoneclick.client.shared.network.platform.AuthenticationDataSource
import theoneclick.client.shared.notifications.NotificationsController

@Inject
internal class UserSettingsViewModel(
    private val authenticationDataSource: AuthenticationDataSource,
    private val notificationsController: NotificationsController,
) : ViewModel() {
    private val isLoading = MutableStateFlow(false)
    val userSettingsScreenState: StateFlow<UserSettingsScreenState> =
        isLoading
            .map(Boolean::toUserSettingsScreenState)
            .stateIn(viewModelScope, SharingStarted.Eagerly, UserSettingsScreenState())

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
                    isLoading.value = true
                }
                .onCompletion {
                    isLoading.value = false
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
