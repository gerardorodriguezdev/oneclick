package theoneclick.shared.contracts.core.dtos.requests

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.dtos.PasswordDto
import theoneclick.shared.contracts.core.dtos.UsernameDto

@Serializable
data class RequestLoginRequestDto(
    val username: UsernameDto,
    val password: PasswordDto,
)
