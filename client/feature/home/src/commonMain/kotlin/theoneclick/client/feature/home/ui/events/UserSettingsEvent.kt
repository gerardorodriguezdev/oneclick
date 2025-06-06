package theoneclick.client.feature.home.ui.events

sealed interface UserSettingsEvent {
    data object LogoutClicked : UserSettingsEvent
    data object SuccessShown : UserSettingsEvent
    data object ErrorShown : UserSettingsEvent
}
