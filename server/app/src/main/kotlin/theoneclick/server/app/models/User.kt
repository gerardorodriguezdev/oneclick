package theoneclick.server.app.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Uuid,
    val username: Username,
    val hashedPassword: HashedPassword,
    val sessionToken: EncryptedToken?,
)
