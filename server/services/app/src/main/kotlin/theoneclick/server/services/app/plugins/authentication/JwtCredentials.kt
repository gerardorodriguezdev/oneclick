package theoneclick.server.services.app.plugins.authentication

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.Uuid

@Serializable
internal class JwtCredentials(
    val jti: Uuid,
    val userId: Uuid,
)