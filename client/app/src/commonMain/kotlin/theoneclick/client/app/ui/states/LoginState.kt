package theoneclick.client.app.ui.states

import theoneclick.client.shared.ui.models.Field

data class LoginState(
    val username: Field = Field(),
    val password: Field = Field(),
    val isRegisterButtonEnabled: Boolean = false,
    val isLoading: Boolean = false,
)
