package theoneclick.server.shared.security

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.util.hex
import kotlinx.serialization.json.Json
import theoneclick.server.shared.models.HashedPassword
import theoneclick.server.shared.models.HashedPassword.Companion.create
import theoneclick.server.shared.models.JwtPayload
import theoneclick.server.shared.plugins.authentication.AuthenticationConstants
import theoneclick.shared.contracts.core.models.Jwt
import theoneclick.shared.timeProvider.TimeProvider
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.text.toCharArray

interface Encryptor {
    fun encrypt(input: String): Result<ByteArray>
    fun decrypt(input: ByteArray): Result<String>
    fun hashPassword(password: String): HashedPassword
    fun verifyPassword(password: String, hashedPassword: HashedPassword): Boolean
    fun jwt(jwtPayload: JwtPayload): Jwt
    fun jwtPayload(jwtPayloadString: String): Result<JwtPayload>
    fun jwtVerifier(): JWTVerifier
}

class DefaultEncryptor(
    private val jwtAudience: String,
    private val jwtIssuer: String,
    private val secretSignKey: String,
    private val secretEncryptionKey: String,
    private val secureRandomProvider: SecureRandomProvider,
    private val timeProvider: TimeProvider,
) : Encryptor {

    private val secretEncryptionKeySpec by lazy {
        SecretKeySpec(hex(secretEncryptionKey), "AES")
    }

    override fun encrypt(input: String): Result<ByteArray> =
        runCatching {
            val cipher = cipher()
            val ivBytes = ByteArray(secretEncryptionKeySpec.encoded.size)

            val secureRandom = secureRandomProvider.secureRandom()
            secureRandom.nextBytes(ivBytes)

            val ivSpec = IvParameterSpec(ivBytes)

            cipher.init(Cipher.ENCRYPT_MODE, secretEncryptionKeySpec, ivSpec)

            val encryptedBytes = cipher.doFinal(input.toByteArray())

            ivBytes + encryptedBytes
        }

    @Suppress("MagicNumber")
    override fun decrypt(input: ByteArray): Result<String> =
        runCatching {
            val iv = input.sliceArray(0 until 16)
            val ivSpec = IvParameterSpec(iv)

            val encryptedBytes = input.sliceArray(16 until input.size)

            val cipher = cipher()
            cipher.init(Cipher.DECRYPT_MODE, secretEncryptionKeySpec, ivSpec)

            val decryptedBytes = cipher.doFinal(encryptedBytes)

            decryptedBytes.decodeToString()
        }

    override fun hashPassword(password: String): HashedPassword =
        create(
            BCrypt.with(secureRandomProvider.secureRandom())
                .hashToString(PASSWORD_VERIFICATION_COST, password.toCharArray())
        )

    override fun verifyPassword(password: String, hashedPassword: HashedPassword): Boolean =
        BCrypt.verifyer().verify(
            password.toCharArray(),
            hashedPassword.value.toCharArray()
        ).verified

    override fun jwt(jwtPayload: JwtPayload): Jwt {
        val currentTime = timeProvider.currentTimeMillis()
        val jwtExpiration = Date(currentTime + AuthenticationConstants.JWT_EXPIRATION_IN_MILLIS)
        val jwtPayloadString = Json.encodeToString(jwtPayload)
        val encryptedJwtPayloadString = encrypt(jwtPayloadString).getOrThrow()
        val encodedJwtPayloadString = Base64.getEncoder().encodeToString(encryptedJwtPayloadString)
        return Jwt.unsafe(
            JWT.create()
                .withAudience(jwtAudience)
                .withIssuer(jwtIssuer)
                .withExpiresAt(jwtExpiration)
                .withClaim(
                    AuthenticationConstants.JWT_PAYLOAD_CLAIM_NAME,
                    encodedJwtPayloadString,
                )
                .sign(Algorithm.HMAC256(secretSignKey))
        )
    }

    override fun jwtPayload(jwtPayloadString: String): Result<JwtPayload> =
        runCatching {
            val decodedJwtPayloadString = Base64.getDecoder().decode(jwtPayloadString)
            val decryptedJwtPayloadString = decrypt(decodedJwtPayloadString).getOrThrow()
            Json.decodeFromString<JwtPayload>(decryptedJwtPayloadString)
        }

    override fun jwtVerifier(): JWTVerifier = JWT
        .require(Algorithm.HMAC256(secretSignKey))
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .withClaimPresence(AuthenticationConstants.JWT_PAYLOAD_CLAIM_NAME)
        .build()

    private fun cipher(): Cipher = Cipher.getInstance(ALGORITHM)

    private companion object {
        const val ALGORITHM = "AES/CBC/PKCS5Padding"
        const val PASSWORD_VERIFICATION_COST = 12
    }
}
