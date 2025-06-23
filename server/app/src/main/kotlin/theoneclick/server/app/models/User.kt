package theoneclick.server.app.models

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.Username
import theoneclick.shared.contracts.core.models.Uuid

@Serializable
data class User(
    val userId: Uuid,
    val username: Username,
    val hashedPassword: HashedPassword,
    val sessionToken: EncryptedToken?,
)
