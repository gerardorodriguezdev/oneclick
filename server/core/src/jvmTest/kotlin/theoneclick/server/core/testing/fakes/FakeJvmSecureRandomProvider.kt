@file:Suppress("NoVarsInConstructor")

package theoneclick.server.core.testing.fakes

import theoneclick.server.core.platform.JvmSecureRandomProvider
import theoneclick.server.core.testing.TestData
import java.security.SecureRandom
import java.util.*

class FakeJvmSecureRandomProvider(
    var seed: Long = TestData.SECURE_RANDOM_SEED,
) : JvmSecureRandomProvider {

    class FakeSecureRandom(private val seed: Long) : SecureRandom() {
        override fun nextBytes(bytes: ByteArray?) = Random(seed).nextBytes(bytes)
    }

    override fun secureRandom(): FakeSecureRandom = FakeSecureRandom(seed)
}
