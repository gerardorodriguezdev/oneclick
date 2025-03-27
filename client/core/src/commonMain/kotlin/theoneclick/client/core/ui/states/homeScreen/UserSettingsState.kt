package theoneclick.client.core.ui.states.homeScreen

data class UserSettingsState(
    val isButtonEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val showSuccess: Boolean = false,
)
