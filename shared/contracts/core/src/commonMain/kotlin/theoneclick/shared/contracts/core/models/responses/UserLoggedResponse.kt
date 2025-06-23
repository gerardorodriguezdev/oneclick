package theoneclick.shared.contracts.core.models.responses

import kotlinx.serialization.Serializable

@Serializable
sealed interface UserLoggedResponse {

    @Serializable
    data object LoggedDto : UserLoggedResponse

    @Serializable
    data object NotLoggedDto : UserLoggedResponse
}
