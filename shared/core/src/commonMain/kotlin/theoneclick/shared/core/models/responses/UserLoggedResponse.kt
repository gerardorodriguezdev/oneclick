package theoneclick.shared.core.models.responses

import kotlinx.serialization.Serializable

@Serializable
sealed interface UserLoggedResponse {

    @Serializable
    data object Logged : UserLoggedResponse

    @Serializable
    data object NotLogged : UserLoggedResponse

    @Serializable
    data object UnknownError : UserLoggedResponse
}
