package oneclick.server.shared.auth.security

import java.security.SecureRandom

interface SecureRandomProvider {
    fun secureRandom(): SecureRandom
}

class DefaultSecureRandomProvider : SecureRandomProvider {
    override fun secureRandom(): SecureRandom = SecureRandom()
}
