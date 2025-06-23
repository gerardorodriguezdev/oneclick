package theoneclick.shared.contracts.core.models.responses

import kotlinx.serialization.Serializable

@Serializable
sealed interface UserLoggedResponseDto {

    @Serializable
    data object LoggedDto : UserLoggedResponseDto

    @Serializable
    data object NotLoggedDto : UserLoggedResponseDto
}
