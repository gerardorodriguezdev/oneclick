package theoneclick.client.features.home.ui.events

internal sealed interface UserSettingsEvent {
    data object LogoutClicked : UserSettingsEvent
}
