package theoneclick.client.core.ui.states

data class LoginState(
    val username: String = "",
    val isUsernameValid: Boolean? = null,

    val password: String = "",
    val isPasswordValid: Boolean? = null,

    val isRegisterButtonEnabled: Boolean = false,

    val isLoading: Boolean = false,
    val showError: Boolean = false,
)
