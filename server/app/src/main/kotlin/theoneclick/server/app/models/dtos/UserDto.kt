package theoneclick.server.app.models.dtos

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.dtos.UsernameDto
import theoneclick.shared.contracts.core.dtos.UuidDto

@Serializable
data class UserDto(
    val userId: UuidDto,
    val username: UsernameDto,
    val hashedPassword: HashedPasswordDto,
    val sessionToken: EncryptedTokenDto?,
)
