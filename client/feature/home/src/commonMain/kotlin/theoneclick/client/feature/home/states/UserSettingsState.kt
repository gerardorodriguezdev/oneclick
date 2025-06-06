package theoneclick.client.feature.home.states

data class UserSettingsState(
    val isButtonEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val showSuccess: Boolean = false,
)
