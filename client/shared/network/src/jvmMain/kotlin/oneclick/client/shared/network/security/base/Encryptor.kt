package oneclick.client.shared.network.security.base

import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.io.encoding.Base64

interface Encryptor {
    fun encrypt(bytes: ByteArray): Result<ByteArray>
    fun decrypt(bytes: ByteArray): Result<ByteArray?>
}

abstract class BaseEncryptor : Encryptor {
    protected abstract val keyStore: KeyStore
    protected abstract val algorithm: String
    protected abstract val blockMode: String
    protected abstract val padding: String
    protected val transformation: String = "$algorithm/$blockMode/$padding"

    protected abstract fun getKey(): SecretKey

    override fun encrypt(bytes: ByteArray): Result<ByteArray> =
        runCatching {
            val secretKey = getKey()
            val cipher = Cipher.getInstance(transformation)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val encrypted = cipher.doFinal(bytes)
            Base64.encode(iv + encrypted).toByteArray()
        }

    override fun decrypt(bytes: ByteArray): Result<ByteArray?> =
        runCatching {
            val decodedBytes = Base64.decode(bytes)
            val iv = decodedBytes.copyOfRange(0, IV_SIZE)
            val data = decodedBytes.copyOfRange(IV_SIZE, decodedBytes.size)
            val secretKey = getKey()
            val cipher = Cipher.getInstance(transformation)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
            cipher.doFinal(data)
        }

    companion object {
        const val KEY_STORE_ALIAS = "secret"
        const val IV_SIZE = 16
        const val KEY_SIZE = 256
    }
}
