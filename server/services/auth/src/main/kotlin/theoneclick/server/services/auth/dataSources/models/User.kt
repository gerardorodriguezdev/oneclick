package theoneclick.server.services.auth.dataSources.models

import kotlinx.serialization.Serializable
import theoneclick.server.shared.auth.models.HashedPassword
import theoneclick.shared.contracts.auth.models.Username
import theoneclick.shared.contracts.core.models.Uuid

@Serializable
data class User(
    val userId: Uuid,
    val username: Username,
    val hashedPassword: HashedPassword,
)