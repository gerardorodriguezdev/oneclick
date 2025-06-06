package theoneclick.client.app.ui.events

sealed interface LoginEvent {
    data class UsernameChanged(val newUsername: String) : LoginEvent
    data class PasswordChanged(val newPassword: String) : LoginEvent

    data object RegisterButtonClicked : LoginEvent
}
