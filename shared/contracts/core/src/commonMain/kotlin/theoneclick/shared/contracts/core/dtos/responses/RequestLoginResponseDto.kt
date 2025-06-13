package theoneclick.shared.contracts.core.dtos.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.dtos.TokenDto

@Serializable
data class RequestLoginResponseDto(val token: TokenDto)
