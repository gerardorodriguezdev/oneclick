package oneclick.server.shared.auth.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import oneclick.server.shared.auth.models.JwtUserId
import oneclick.shared.contracts.auth.models.Jwt
import oneclick.shared.contracts.core.models.Uuid
import oneclick.shared.contracts.core.models.Uuid.Companion.toUuid
import oneclick.shared.security.encryption.base.Encryptor
import oneclick.shared.timeProvider.TimeProvider
import java.util.*

interface JwtProvider {
    val jwtVerifier: JWTVerifier
    val jwtClaim: String
    val jwtRealm: String
    val jwtExpirationTimeInMillis: Long
    val jwtSessionName: String

    fun jwt(userId: Uuid): Jwt
    fun userId(jwtUserId: JwtUserId): Uuid?
}

class EncryptedJwtProvider(
    override val jwtRealm: String,
    private val jwtAudience: String,
    private val jwtIssuer: String,
    private val secretSignKey: String,
    private val timeProvider: TimeProvider,
    private val encryptor: Encryptor,
    private val uuidProvider: UuidProvider,
) : JwtProvider {

    override val jwtExpirationTimeInMillis: Long = 60_000L
    override val jwtSessionName: String = "user_session"
    override val jwtClaim: String = "payload"

    override val jwtVerifier: JWTVerifier = JWT
        .require(Algorithm.HMAC256(secretSignKey))
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .withClaimPresence(jwtClaim)
        .build()

    override fun jwt(userId: Uuid): Jwt {
        val currentTime = timeProvider.currentTimeMillis()
        val jwtExpiration = Date(currentTime + jwtExpirationTimeInMillis)
        val jwtUserId = jwtUserId(userId)
        return Jwt.unsafe(
            JWT.create()
                .withJWTId(uuidProvider.uuid().value)
                .withAudience(jwtAudience)
                .withIssuer(jwtIssuer)
                .withExpiresAt(jwtExpiration)
                .withClaim(
                    jwtClaim,
                    jwtUserId.value,
                )
                .sign(Algorithm.HMAC256(secretSignKey))
        )
    }

    override fun userId(jwtUserId: JwtUserId): Uuid? {
        val decodedUserIdString = Base64.getDecoder().decode(jwtUserId.value)
        val decryptedUserIdString = encryptor.decrypt(decodedUserIdString).getOrNull() ?: return null
        return decryptedUserIdString.toUuid()
    }

    private fun jwtUserId(userId: Uuid): JwtUserId {
        val encryptedUserId = encryptor.encrypt(userId.value).getOrThrow()
        val encodedUserIdString = Base64.getEncoder().encodeToString(encryptedUserId)
        return JwtUserId.unsafe(encodedUserIdString)
    }
}