package oneclick.client.apps.features.home.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import oneclick.client.apps.features.home.mappers.toUserSettingsScreenState
import oneclick.client.apps.features.home.ui.screens.UserSettingsEvent
import oneclick.client.apps.features.home.ui.screens.UserSettingsScreenState
import oneclick.client.apps.user.features.home.generated.resources.Res
import oneclick.client.apps.user.features.home.generated.resources.userSettingsScreen_snackbar_logout
import oneclick.client.apps.user.features.home.generated.resources.userSettingsScreen_snackbar_unknownError
import oneclick.client.shared.network.models.LogoutResult
import oneclick.client.shared.network.platform.AuthenticationDataSource
import oneclick.client.apps.user.notifications.NotificationsController
import org.jetbrains.compose.resources.getString

@Inject
internal class UserSettingsViewModel(
    private val authenticationDataSource: AuthenticationDataSource,
    private val notificationsController: NotificationsController,
) : ViewModel() {
    private val isLoading = MutableStateFlow(false)
    val userSettingsScreenState: StateFlow<UserSettingsScreenState> =
        isLoading
            .map(Boolean::toUserSettingsScreenState)
            .stateIn(
                viewModelScope, SharingStarted.Eagerly,
                UserSettingsScreenState()
            )

    private var logoutJob: Job? = null

    fun onEvent(event: UserSettingsEvent) {
        when (event) {
            is UserSettingsEvent.LogoutClicked -> handleLogoutClicked()
        }
    }

    private fun handleLogoutClicked() {
        logoutJob?.cancel()

        logoutJob = viewModelScope.launch {
            isLoading.value = true

            val logoutResult = authenticationDataSource.logout()
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

            isLoading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()

        logoutJob?.cancel()
    }
}
