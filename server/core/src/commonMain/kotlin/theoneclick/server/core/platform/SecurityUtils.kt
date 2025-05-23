package theoneclick.server.core.platform

import theoneclick.server.core.models.EncryptedToken
import theoneclick.server.core.models.HashedPassword

interface SecurityUtils {
    fun encrypt(input: String): ByteArray
    fun decrypt(input: ByteArray): String
    fun hashPassword(password: String): HashedPassword
    fun verifyPassword(password: String, hashedPassword: HashedPassword): Boolean
    fun encryptedToken(): EncryptedToken
}
