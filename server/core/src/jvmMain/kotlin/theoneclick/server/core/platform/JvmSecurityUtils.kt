package theoneclick.server.core.platform

import at.favre.lib.crypto.bcrypt.BCrypt
import io.ktor.util.*
import theoneclick.server.core.models.EncryptedToken
import theoneclick.server.core.models.HashedPassword
import theoneclick.shared.timeProvider.TimeProvider
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.text.toCharArray
import theoneclick.server.core.models.EncryptedToken.Companion.create as createEncryptedToken
import theoneclick.server.core.models.HashedPassword.Companion.create as createHashedPassword

class JvmSecurityUtils(
    private val secretEncryptionKey: String,
    private val jvmSecureRandomProvider: JvmSecureRandomProvider,
    private val timeProvider: TimeProvider,
) : SecurityUtils {

    private val secretEncryptionKeySpec by lazy {
        SecretKeySpec(hex(secretEncryptionKey), "AES")
    }

    override fun encrypt(input: String): ByteArray {
        val cipher = cipher()
        val ivBytes = ByteArray(secretEncryptionKeySpec.encoded.size)

        val secureRandom = jvmSecureRandomProvider.secureRandom()
        secureRandom.nextBytes(ivBytes)

        val ivSpec = IvParameterSpec(ivBytes)

        cipher.init(Cipher.ENCRYPT_MODE, secretEncryptionKeySpec, ivSpec)

        val encryptedBytes = cipher.doFinal(input.toByteArray())

        return ivBytes + encryptedBytes
    }

    @Suppress("MagicNumber")
    override fun decrypt(input: ByteArray): String {
        val iv = input.sliceArray(0 until 16)
        val ivSpec = IvParameterSpec(iv)

        val encryptedBytes = input.sliceArray(16 until input.size)

        val cipher = cipher()
        cipher.init(Cipher.DECRYPT_MODE, secretEncryptionKeySpec, ivSpec)

        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return decryptedBytes.decodeToString()
    }

    override fun hashPassword(password: String): HashedPassword =
        createHashedPassword(
            BCrypt.with(jvmSecureRandomProvider.secureRandom())
                .hashToString(PASSWORD_VERIFICATION_COST, password.toCharArray())
        )

    override fun verifyPassword(password: String, hashedPassword: HashedPassword): Boolean =
        BCrypt.verifyer().verify(password.toCharArray(), hashedPassword.value.toCharArray()).verified

    override fun encryptedToken(): EncryptedToken {
        val secureRandom = jvmSecureRandomProvider.secureRandom()

        val bytes = ByteArray(TOKEN_SIZE)
        secureRandom.nextBytes(bytes)

        val plainToken = bytes.decodeToString()
        val encryptedToken = encrypt(plainToken)
        val encryptedTokenValue = Base64.getEncoder().encodeToString(encryptedToken)
        return createEncryptedToken(
            value = encryptedTokenValue,
            creationTimeInMillis = timeProvider.currentTimeMillis(),
        )
    }

    private fun cipher(): Cipher = Cipher.getInstance(ALGORITHM)

    private companion object {
        const val ALGORITHM = "AES/CBC/PKCS5Padding"
        const val TOKEN_SIZE = 32
        const val PASSWORD_VERIFICATION_COST = 12
    }
}
