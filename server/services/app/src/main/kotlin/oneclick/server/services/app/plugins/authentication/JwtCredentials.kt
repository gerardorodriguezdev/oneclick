package oneclick.server.services.app.plugins.authentication

import kotlinx.serialization.Serializable
import oneclick.server.shared.authentication.security.HomeJwtProvider
import oneclick.server.shared.authentication.security.UserJwtProvider
import oneclick.shared.contracts.core.models.Uuid

internal sealed interface JwtCredentials {
    val jti: Uuid
    val expirationTime: Long

    @Serializable
    data class HomeJwtCredentials(
        override val jti: Uuid,
        val userId: Uuid,
        val homeId: Uuid,
    ) : JwtCredentials {
        override val expirationTime: Long = HomeJwtProvider.JWT_EXPIRATION_TIME
    }

    @Serializable
    data class UserJwtCredentials(
        override val jti: Uuid,
        val userId: Uuid,
    ) : JwtCredentials {
        override val expirationTime: Long = UserJwtProvider.JWT_EXPIRATION_TIME
    }
}