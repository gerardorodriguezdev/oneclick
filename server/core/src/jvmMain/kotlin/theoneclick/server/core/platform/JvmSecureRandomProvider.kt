package theoneclick.server.core.platform

import java.security.SecureRandom

interface JvmSecureRandomProvider {
    fun secureRandom(): SecureRandom
}

class DefaultJvmSecureRandomProvider : JvmSecureRandomProvider {
    override fun secureRandom(): SecureRandom = SecureRandom()
}
