package theoneclick.server.core.platform

import theoneclick.server.core.data.models.HashedPassword
import theoneclick.server.core.testing.TestData
import theoneclick.server.core.testing.base.IntegrationTest
import theoneclick.server.core.testing.fakes.FakeJvmSecureRandomProvider
import theoneclick.shared.testing.timeProvider.FakeTimeProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class JvmSecurityUtilsTest : IntegrationTest() {
    private val jvmSecurityUtils = JvmSecurityUtils(
        secretEncryptionKey = TestData.SECRET_ENCRYPTION_KEY,
        jvmSecureRandomProvider = FakeJvmSecureRandomProvider(),
        timeProvider = FakeTimeProvider(fakeCurrentTimeInMillis = TestData.CURRENT_TIME_IN_MILLIS),
    )

    @Test
    fun `GIVEN inputData WHEN encrypt and decrypt are called THEN input data is encrypted and decrypted`() {
        val inputData = "abc"

        val encryptedData = jvmSecurityUtils.encrypt("abc")
        assertNotEquals(illegal = inputData, actual = encryptedData.decodeToString())

        val decryptedData = jvmSecurityUtils.decrypt(encryptedData)
        assertEquals(expected = inputData, actual = decryptedData)
    }

    @Test
    fun `GIVEN valid password WHEN verifyPassword with stored hashed password THEN returns true`() {
        val actualValidationResult = jvmSecurityUtils.verifyPassword(
            password = TestData.RAW_PASSWORD,
            hashedPassword = HashedPassword(TestData.HASHED_PASSWORD),
        )

        assertTrue(actualValidationResult)
    }

    @Test
    fun `WHEN encryptedToken requested THEN returns valid token`() {
        val actual = jvmSecurityUtils.encryptedToken()

        assertEquals(expected = TestData.ENCRYPTED_TOKEN_VALUE, actual = actual.value)
        assertEquals(expected = TestData.CURRENT_TIME_IN_MILLIS, actual = actual.creationTimeInMillis)
    }

    @Test
    fun `WHEN hashPassword requested THEN returns valid hashedPassword`() {
        val actual = jvmSecurityUtils.hashPassword(TestData.RAW_PASSWORD)

        assertEquals(expected = TestData.HASHED_PASSWORD, actual = actual.value)
    }
}
