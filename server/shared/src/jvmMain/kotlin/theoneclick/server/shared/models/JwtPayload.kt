package theoneclick.server.shared.models

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.Uuid

@Serializable
data class JwtPayload(
    val userId: Uuid,
)
