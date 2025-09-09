package theoneclick.shared.contracts.auth.models.responses

import kotlinx.serialization.Serializable

@Serializable
sealed interface UserLoggedResponse {

    @Serializable
    data object Logged : UserLoggedResponse

    @Serializable
    data object NotLogged : UserLoggedResponse
}