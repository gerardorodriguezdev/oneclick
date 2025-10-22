package oneclick.server.shared.auth.security

import io.ktor.util.*
import oneclick.shared.security.SecureRandomProvider
import oneclick.shared.security.encryption.base.BaseEncryptor
import java.security.Key
import javax.crypto.spec.SecretKeySpec

class KtorKeystoreEncryptor(
    private val secretEncryptionKey: String,
    secureRandomProvider: SecureRandomProvider
) : BaseEncryptor(secureRandomProvider) {
    override val transformation: String = "$ALGORITHM/CBC/PKCS7Padding"

    override fun secretKey(): Key = SecretKeySpec(hex(secretEncryptionKey), ALGORITHM)

    private companion object {
        const val ALGORITHM = "AES"
    }
}