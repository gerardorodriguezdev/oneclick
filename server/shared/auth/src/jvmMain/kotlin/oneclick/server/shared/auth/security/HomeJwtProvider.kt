package oneclick.server.shared.auth.security

import com.auth0.jwt.interfaces.Verification
import oneclick.server.shared.auth.security.base.BaseEncryptedJwtProvider
import oneclick.shared.contracts.auth.models.Jwt
import oneclick.shared.contracts.core.models.Uuid
import oneclick.shared.security.encryption.base.Encryptor
import oneclick.shared.timeProvider.TimeProvider

class HomeJwtProvider(
    secretSignKey: String,
    audience: String,
    issuer: String,
    timeProvider: TimeProvider,
    encryptor: Encryptor,
    uuidProvider: UuidProvider,
) : BaseEncryptedJwtProvider(
    secretSignKey = secretSignKey,
    audience = audience,
    issuer = issuer,
    expirationTime = JWT_EXPIRATION_TIME,
    timeProvider = timeProvider,
    encryptor = encryptor,
    uuidProvider = uuidProvider,
) {
    override val verifierSetup: Verification.() -> Verification = {
        withClaimPresence(USER_ID_CLAIM)
        withClaimPresence(HOME_ID_CLAIM)
    }

    fun jwt(userId: Uuid, homeId: Uuid): Jwt =
        jwt(
            claims = buildMap {
                val userId = jwtId(userId)
                put(USER_ID_CLAIM, userId.value)

                val homeId = jwtId(homeId)
                put(HOME_ID_CLAIM, homeId.value)
            }
        )

    companion object {
        private const val USER_ID_CLAIM = "userId"
        private const val HOME_ID_CLAIM = "homeId"
        const val JWT_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 30L
    }
}