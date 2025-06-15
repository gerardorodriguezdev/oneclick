package theoneclick.server.app.models

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.dtos.HomeDto
import theoneclick.shared.contracts.core.dtos.UsernameDto
import theoneclick.shared.contracts.core.dtos.UuidDto

@Serializable
data class UserDto(
    val id: UuidDto,
    val username: UsernameDto,
    val hashedPassword: HashedPasswordDto,
    val sessionToken: EncryptedTokenDto?,
    val homes: List<HomeDto>,
)
