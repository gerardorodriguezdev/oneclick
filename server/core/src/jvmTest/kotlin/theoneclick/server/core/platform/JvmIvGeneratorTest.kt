package theoneclick.server.core.platform

import theoneclick.server.core.testing.fakes.FakeJvmSecureRandomProvider
import kotlin.test.Test
import kotlin.test.assertEquals

class JvmIvGeneratorTest {
    private val jvmIvGenerator = JvmIvGenerator(FakeJvmSecureRandomProvider())

    @Test
    fun `GIVEN valid secure random WHEN iv is called THEN returns byte array`() {
        val iv = jvmIvGenerator.iv(1)

        assertEquals(expected = "s", actual = iv.decodeToString())
    }
}
