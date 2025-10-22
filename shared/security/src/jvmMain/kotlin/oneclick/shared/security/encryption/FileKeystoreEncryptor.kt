package oneclick.shared.security.encryption

import oneclick.shared.security.SecureRandomProvider
import oneclick.shared.security.encryption.base.BaseEncryptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.Key
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class FileKeystoreEncryptor(
    private val keyStorePath: String,
    private val keyStorePassword: CharArray,
    secureRandomProvider: SecureRandomProvider
) : BaseEncryptor(secureRandomProvider) {
    override val transformation: String = "$ALGORITHM/CBC/PKCS5Padding"
    private val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())

    init {
        keyStore.load(FileInputStream(keyStorePath), keyStorePassword)
    }

    override fun secretKey(): Key {
        val existingKey = keyStore.getEntry(
            KEY_STORE_ALIAS,
            KeyStore.PasswordProtection(keyStorePassword)
        )

        return if (existingKey is KeyStore.SecretKeyEntry) {
            existingKey.secretKey
        } else {
            val secretKey = generateSecretKey()
            storeSecretKey(secretKey)
            secretKey
        }
    }

    private fun generateSecretKey(): SecretKey {
        val keyGenerator = keyGenerator()
        keyGenerator.init(
            KEY_SIZE,
            secureRandomProvider.secureRandom(),
        )
        return keyGenerator.generateKey()
    }

    private fun storeSecretKey(secretKey: SecretKey) {
        keyStore.setEntry(
            KEY_STORE_ALIAS,
            KeyStore.SecretKeyEntry(secretKey),
            KeyStore.PasswordProtection(keyStorePassword)
        )

        FileOutputStream(keyStorePath).use { os ->
            keyStore.store(os, keyStorePassword)
        }
    }

    private fun keyGenerator(): KeyGenerator = KeyGenerator.getInstance(ALGORITHM)

    private companion object Companion {
        const val KEY_STORE_ALIAS = "secret"
        const val KEY_SIZE = 256
        const val ALGORITHM = "AES"
    }
}