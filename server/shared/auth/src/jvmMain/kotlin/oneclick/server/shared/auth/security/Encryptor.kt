package oneclick.server.shared.auth.security

import at.favre.lib.crypto.bcrypt.BCrypt
import io.ktor.util.hex
import oneclick.server.shared.auth.models.HashedPassword
import oneclick.shared.contracts.auth.models.Password
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.text.toCharArray

interface Encryptor {
    fun encrypt(input: String): Result<ByteArray>
    fun decrypt(input: ByteArray): Result<String>

    fun hashPassword(password: Password): HashedPassword
    fun verifyPassword(password: Password, hashedPassword: HashedPassword): Boolean
}

class DefaultEncryptor(
    private val secretEncryptionKey: String,
    private val secureRandomProvider: SecureRandomProvider,
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

    override fun hashPassword(password: Password): HashedPassword =
        HashedPassword.unsafe(
            BCrypt.with(secureRandomProvider.secureRandom())
                .hashToString(PASSWORD_VERIFICATION_COST, password.value.toCharArray())
        )

    override fun verifyPassword(password: Password, hashedPassword: HashedPassword): Boolean =
        BCrypt.verifyer().verify(
            password.value.toCharArray(),
            hashedPassword.value.toCharArray()
        ).verified

    private fun cipher(): Cipher = Cipher.getInstance(ALGORITHM)

    private companion object {
        const val ALGORITHM = "AES/CBC/PKCS5Padding"
        const val PASSWORD_VERIFICATION_COST = 12
    }
}
