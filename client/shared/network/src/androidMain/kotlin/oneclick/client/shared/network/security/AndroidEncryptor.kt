package oneclick.client.shared.network.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import oneclick.client.shared.network.security.base.BaseEncryptor
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class AndroidEncryptor : BaseEncryptor() {
    override val keyStore: KeyStore = KeyStore.getInstance(KEY_STORE_TYPE)
    override val algorithm: String = KeyProperties.KEY_ALGORITHM_AES
    override val blockMode: String = KeyProperties.BLOCK_MODE_CBC
    override val padding: String = KeyProperties.ENCRYPTION_PADDING_PKCS7

    init {
        keyStore.load(null)
    }

    override fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(KEY_STORE_ALIAS, null)

        return if (existingKey is KeyStore.SecretKeyEntry) {
            existingKey.secretKey
        } else {
            generateKey()
        }
    }

    private fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(algorithm, KEY_STORE_TYPE)
        keyGenerator.init(
            KeyGenParameterSpec
                .Builder(
                    KEY_STORE_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                .setBlockModes(blockMode)
                .setEncryptionPaddings(padding)
                .setKeySize(KEY_SIZE)
                .build()
        )
        return keyGenerator.generateKey()
    }

    private companion object {
        const val KEY_STORE_TYPE = "AndroidKeyStore"
    }
}
