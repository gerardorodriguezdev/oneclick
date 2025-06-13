package theoneclick.shared.contracts.core.dtos.requests

import kotlinx.serialization.Serializable

@Serializable
data class RequestLoginRequestDto(
    val username: String,
    val password: String,
)
