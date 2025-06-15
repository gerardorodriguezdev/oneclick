package theoneclick.server.app.security

import at.favre.lib.crypto.bcrypt.BCrypt
import io.ktor.util.hex
import theoneclick.server.app.models.EncryptedToken
import theoneclick.server.app.models.HashedPassword
import theoneclick.shared.timeProvider.TimeProvider
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.text.toCharArray
import theoneclick.server.app.models.EncryptedToken.Companion.create as createEncryptedToken
import theoneclick.server.app.models.HashedPassword.Companion.create as createHashedPassword

interface Encryptor {
    fun encrypt(input: String): Result<ByteArray>
    fun decrypt(input: ByteArray): Result<String>
    fun hashPassword(password: String): HashedPassword
    fun verifyPassword(password: String, hashedPassword: HashedPassword): Boolean
    fun encryptedToken(): EncryptedToken
}

class DefaultEncryptor(
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
        createHashedPassword(
            BCrypt.with(secureRandomProvider.secureRandom())
                .hashToString(PASSWORD_VERIFICATION_COST, password.toCharArray())
        )

    override fun verifyPassword(password: String, hashedPassword: HashedPassword): Boolean =
        BCrypt.verifyer().verify(
            password.toCharArray(), hashedPassword.value.toCharArray()
        ).verified

    override fun encryptedToken(): EncryptedToken {
        val secureRandom = secureRandomProvider.secureRandom()

        val bytes = ByteArray(TOKEN_SIZE)
        secureRandom.nextBytes(bytes)

        val plainToken = bytes.decodeToString()
        val encryptedToken = encrypt(plainToken).getOrThrow()
        val encryptedTokenValue = Base64.getEncoder().encodeToString(encryptedToken)
        return createEncryptedToken(
            token = encryptedTokenValue,
            creationTimeInMillis = timeProvider.currentTimeMillis(),
        )
    }

    private fun cipher(): Cipher = Cipher.getInstance(ALGORITHM)

    private companion object Companion {
        const val ALGORITHM = "AES/CBC/PKCS5Padding"
        const val TOKEN_SIZE = 32
        const val PASSWORD_VERIFICATION_COST = 12
    }
}
