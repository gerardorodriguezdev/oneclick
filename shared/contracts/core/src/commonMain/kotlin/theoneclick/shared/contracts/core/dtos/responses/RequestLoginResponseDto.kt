package theoneclick.shared.contracts.core.dtos.responses

import kotlinx.serialization.Serializable

@Serializable
data class RequestLoginResponseDto(val token: String)
