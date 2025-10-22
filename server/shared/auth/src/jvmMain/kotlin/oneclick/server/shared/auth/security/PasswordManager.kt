package oneclick.server.shared.auth.security

import at.favre.lib.crypto.bcrypt.BCrypt
import oneclick.server.shared.auth.models.HashedPassword
import oneclick.shared.contracts.auth.models.Password
import oneclick.shared.security.SecureRandomProvider

interface PasswordManager {
    fun hashPassword(password: Password): HashedPassword
    fun verifyPassword(password: Password, hashedPassword: HashedPassword): Boolean
}

data class BcryptPasswordManager(
    private val secureRandomProvider: SecureRandomProvider,
) : PasswordManager {
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

    private companion object {
        const val PASSWORD_VERIFICATION_COST = 12
    }
}