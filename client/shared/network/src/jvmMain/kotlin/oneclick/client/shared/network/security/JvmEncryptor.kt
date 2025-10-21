package oneclick.client.shared.network.security

import oneclick.client.shared.network.security.base.BaseEncryptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class JvmEncryptor(
    private val keyStorePath: String,
    private val keyStorePassword: CharArray,
    private val secureRandomProvider: () -> SecureRandom, //TODO: Provider
) : BaseEncryptor() {
    override val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    override val algorithm: String = "AES"
    override val blockMode: String = "CBC"
    override val padding: String = "PKCS7Padding"

    init {
        try {
            keyStore.load(FileInputStream(keyStorePath), keyStorePassword)
        } catch (_: Exception) {
            keyStore.load(null, keyStorePassword)
        }
    }

    override fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(
            KEY_STORE_ALIAS,
            KeyStore.PasswordProtection(keyStorePassword)
        )

        return if (existingKey is KeyStore.SecretKeyEntry) {
            existingKey.secretKey
        } else {
            val secretKey = generateKey()
            storeKey(secretKey)
            secretKey
        }
    }

    private fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(algorithm)
        keyGenerator.init(
            KEY_SIZE,
            secureRandomProvider(),
        )
        return keyGenerator.generateKey()
    }

    private fun storeKey(secretKey: SecretKey) {
        keyStore.setEntry(
            KEY_STORE_ALIAS,
            KeyStore.SecretKeyEntry(secretKey),
            KeyStore.PasswordProtection(keyStorePassword)
        )

        FileOutputStream(keyStorePath).use { os ->
            keyStore.store(os, keyStorePassword)
        }
    }
}
