package oneclick.server.services.app.dataSources.models

import kotlinx.serialization.Serializable
import oneclick.server.shared.authentication.models.HashedPassword
import oneclick.shared.contracts.auth.models.Username
import oneclick.shared.contracts.core.models.Uuid

@Serializable
internal data class User(
    val userId: Uuid,
    val username: Username,
    val hashedPassword: HashedPassword,
)