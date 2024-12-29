@file:Suppress("NoVarsInConstructor")

package theoneclick.server.core.testing.fakes

import theoneclick.server.core.data.models.EncryptedToken
import theoneclick.server.core.data.models.HashedPassword
import theoneclick.server.core.data.models.HashedPassword.Companion.create
import theoneclick.server.core.platform.SecurityUtils
import theoneclick.server.core.testing.TestData

class FakeSecurityUtils(
    var encryptResult: ByteArray? = null,
    var decryptResult: String? = null,
    var hashPasswordResult: String = TestData.HASHED_PASSWORD,
    var verifyPasswordResult: Boolean = false,
    var encryptedTokenResult: EncryptedToken = TestData.encryptedToken,
) : SecurityUtils {

    override fun encrypt(input: String): ByteArray = encryptResult ?: input.toByteArray()

    override fun decrypt(input: ByteArray): String = decryptResult ?: input.decodeToString()

    override fun hashPassword(password: String): HashedPassword = create(hashPasswordResult)

    override fun verifyPassword(password: String, hashedPassword: HashedPassword): Boolean =
        verifyPasswordResult

    override fun encryptedToken(): EncryptedToken = encryptedTokenResult
}
