package theoneclick.server.shared.auth.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import kotlinx.serialization.json.Json
import theoneclick.server.shared.auth.models.JwtPayload
import theoneclick.shared.contracts.auth.models.Jwt
import theoneclick.shared.timeProvider.TimeProvider
import java.util.*

interface JwtProvider {
    val jwtVerifier: JWTVerifier
    val jwtClaim: String
    val jwtRealm: String
    val jwtExpirationTimeInMillis: Long
    val jwtSessionName: String

    fun jwt(jwtPayload: JwtPayload): Jwt
    fun jwtPayload(jwtPayloadString: String): Result<JwtPayload>
}

class DefaultJwtProvider(
    override val jwtRealm: String,
    private val jwtAudience: String,
    private val jwtIssuer: String,
    private val secretSignKey: String,
    private val timeProvider: TimeProvider,
    private val encryptor: Encryptor,
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

    override fun jwt(jwtPayload: JwtPayload): Jwt {
        val currentTime = timeProvider.currentTimeMillis()
        val jwtExpiration = Date(currentTime + jwtExpirationTimeInMillis)
        val jwtPayloadString = Json.encodeToString(jwtPayload)
        val encryptedJwtPayloadString = encryptor.encrypt(jwtPayloadString).getOrThrow()
        val encodedJwtPayloadString = Base64.getEncoder().encodeToString(encryptedJwtPayloadString)
        return Jwt.unsafe(
            JWT.create()
                .withAudience(jwtAudience)
                .withIssuer(jwtIssuer)
                .withExpiresAt(jwtExpiration)
                .withClaim(
                    jwtClaim,
                    encodedJwtPayloadString,
                )
                .sign(Algorithm.HMAC256(secretSignKey))
        )
    }

    override fun jwtPayload(jwtPayloadString: String): Result<JwtPayload> =
        runCatching {
            val decodedJwtPayloadString = Base64.getDecoder().decode(jwtPayloadString)
            val decryptedJwtPayloadString = encryptor.decrypt(decodedJwtPayloadString).getOrThrow()
            Json.decodeFromString<JwtPayload>(decryptedJwtPayloadString)
        }
}