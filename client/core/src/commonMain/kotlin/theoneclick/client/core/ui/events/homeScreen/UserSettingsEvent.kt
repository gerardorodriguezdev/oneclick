package theoneclick.client.core.ui.events.homeScreen

sealed interface UserSettingsEvent {
    data object LogoutClicked : UserSettingsEvent
    data object SuccessShown : UserSettingsEvent
    data object ErrorShown : UserSettingsEvent
}
