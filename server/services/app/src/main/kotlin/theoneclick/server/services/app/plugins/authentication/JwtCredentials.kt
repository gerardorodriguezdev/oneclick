package oneclick.server.services.app.plugins.authentication

import kotlinx.serialization.Serializable
import oneclick.shared.contracts.core.models.Uuid

@Serializable
internal class JwtCredentials(
    val jti: Uuid,
    val userId: Uuid,
)