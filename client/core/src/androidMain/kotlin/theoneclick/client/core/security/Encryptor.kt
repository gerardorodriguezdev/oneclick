package theoneclick.client.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import theoneclick.shared.core.platform.AppLogger
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface Encryptor {
    fun encrypt(bytes: ByteArray): Result<ByteArray>
    fun decrypt(bytes: ByteArray): Result<ByteArray?>
}

class AndroidEncryptor(private val appLogger: AppLogger) : Encryptor {
    private val keyStore = KeyStore.getInstance(KEY_STORE_TYPE)

    init {
        keyStore.load(null)
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun encrypt(bytes: ByteArray): Result<ByteArray> =
        runCatching {
            val secretKey = getKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val encrypted = cipher.doFinal(bytes)
            Base64.encode(iv + encrypted).toByteArray()
        }

    @OptIn(ExperimentalEncodingApi::class)
    override fun decrypt(bytes: ByteArray): Result<ByteArray?> =
        runCatching {
            val decodedBytes = Base64.decode(bytes)
            val iv = decodedBytes.copyOfRange(0, IV_SIZE)
            val data = decodedBytes.copyOfRange(IV_SIZE, decodedBytes.size)
            val secretKey = getKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
            cipher.doFinal(data)
        }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(KEY_STORE_ALIAS, null)

        return if (existingKey is KeyStore.SecretKeyEntry) {
            existingKey.secretKey
        } else {
            generateKey()
        }
    }

    private fun generateKey(): SecretKey {
        appLogger.i("Generating secret key")

        val keyGenerator = KeyGenerator.getInstance(ALGORITHM, KEY_STORE_TYPE)
        keyGenerator.init(
            KeyGenParameterSpec
                .Builder(
                    KEY_STORE_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                .setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(PADDING)
                .setKeySize(KEY_SIZE)
                .build()
        )
        return keyGenerator.generateKey()
    }

    private companion object {
        const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        const val KEY_STORE_TYPE = "AndroidKeyStore"
        const val KEY_STORE_ALIAS = "secret"
        const val IV_SIZE = 16
        const val KEY_SIZE = 256
    }
}