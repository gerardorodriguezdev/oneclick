package oneclick.shared.security.encryption.base

import oneclick.shared.security.SecureRandomProvider
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

interface Encryptor {
    fun encrypt(input: String): Result<ByteArray>
    fun decrypt(input: ByteArray): Result<String>
}

abstract class BaseEncryptor(
    protected val secureRandomProvider: SecureRandomProvider,
) : Encryptor {
    protected abstract val transformation: String

    protected abstract fun secretKey(): Key

    override fun encrypt(input: String): Result<ByteArray> =
        runCatching {
            val secretKey = secretKey()
            val ivBytes = ByteArray(IV_SIZE)
            val secureRandom = secureRandomProvider.secureRandom()
            secureRandom.nextBytes(ivBytes)
            val ivSpec = IvParameterSpec(ivBytes)

            val cipher = cipher()
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
            val encryptedBytes = cipher.doFinal(input.toByteArray())

            ivBytes + encryptedBytes
        }

    override fun decrypt(input: ByteArray): Result<String> =
        runCatching {
            val secretKey = secretKey()
            val iv = input.sliceArray(0 until IV_SIZE)
            val ivSpec = IvParameterSpec(iv)
            val encryptedBytes = input.sliceArray(IV_SIZE until input.size)

            val cipher = cipher()
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
            val decryptedBytes = cipher.doFinal(encryptedBytes)

            decryptedBytes.decodeToString()
        }

    private fun cipher(): Cipher = Cipher.getInstance(transformation)

    private companion object {
        const val IV_SIZE = 16
    }
}
