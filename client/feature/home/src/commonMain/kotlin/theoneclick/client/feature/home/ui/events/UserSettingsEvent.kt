package theoneclick.client.feature.home.ui.events

internal sealed interface UserSettingsEvent {
    data object LogoutClicked : UserSettingsEvent
    data object SuccessShown : UserSettingsEvent
    data object ErrorShown : UserSettingsEvent
}
