package theoneclick.shared.contracts.core.dtos.responses

import kotlinx.serialization.Serializable

@Serializable
sealed interface UserLoggedResponseDto {

    @Serializable
    data object LoggedDto : UserLoggedResponseDto

    @Serializable
    data object NotLoggedDto : UserLoggedResponseDto
}
