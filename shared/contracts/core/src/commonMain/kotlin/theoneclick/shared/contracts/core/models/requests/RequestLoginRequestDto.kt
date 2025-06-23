package theoneclick.shared.contracts.core.models.requests

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.Password
import theoneclick.shared.contracts.core.models.Username

@Serializable
data class RequestLoginRequestDto(
    val username: Username,
    val password: Password,
)
