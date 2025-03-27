package theoneclick.client.core.security

import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.builtins.serializer
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import theoneclick.client.core.dataSources.AndroidEncryptedPreferences
import theoneclick.client.core.dataSources.EncryptedPreferences
import theoneclick.client.core.testing.FakeEncryptor
import theoneclick.shared.core.platform.appLogger
import theoneclick.shared.testing.dispatchers.FakeDispatchersProvider
import theoneclick.shared.testing.extensions.generateRandomString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AndroidEncryptedPreferencesTest {

    @get:Rule
    val temporalFolder = TemporaryFolder
        .builder()
        .assureDeletion()
        .build()

    private val fakeEncryptor = FakeEncryptor()

    @Test
    fun GIVEN_noPreference_WHEN_preference_THEN_returnsNull() = runTest {
        val encryptedPreferences = encryptedPreferences()
        assertNull(encryptedPreferences.preference(PREFERENCE_KEY, String.serializer()))
    }

    @Test
    fun GIVEN_preference_WHEN_preference_THEN_returnsValue() = runTest {
        val encryptedPreferences = encryptedPreferences()
        val putPreferenceResult =
            encryptedPreferences.putPreference(PREFERENCE_KEY, PREFERENCE_VALUE, String.serializer())

        assertTrue(putPreferenceResult)
        assertEquals(
            expected = PREFERENCE_VALUE,
            actual = encryptedPreferences.preference(PREFERENCE_KEY, String.serializer())
        )
    }

    @Test
    fun GIVEN_preference_WHEN_clear_THEN_clearsPreferences() = runTest {
        val encryptedPreferences = encryptedPreferences()
        encryptedPreferences.putPreference(PREFERENCE_KEY, PREFERENCE_VALUE, String.serializer())

        assertEquals(
            expected = PREFERENCE_VALUE,
            actual = encryptedPreferences.preference(PREFERENCE_KEY, String.serializer())
        )

        encryptedPreferences.clearPreference(PREFERENCE_KEY)

        assertNull(encryptedPreferences.preference(PREFERENCE_KEY, String.serializer()))
    }

    @Test
    fun GIVEN_invalidSavedPreference_WHEN_preference_THEN_returnsNull() = runTest {
        val encryptedPreferences = encryptedPreferences()
        encryptedPreferences.putPreference(PREFERENCE_KEY, PREFERENCE_VALUE, String.serializer())
        fakeEncryptor.decryptResult = Result.failure(Exception("error"))

        assertNull(encryptedPreferences.preference(PREFERENCE_KEY, String.serializer()))

        fakeEncryptor.decryptResult = null
        assertNull(encryptedPreferences.preference(PREFERENCE_KEY, String.serializer()))
    }

    @Test
    fun GIVEN_invalidPutPreference_WHEN_putPreference_THEN_returnsFalse() = runTest {
        val encryptedPreferences = encryptedPreferences()
        fakeEncryptor.encryptResult = Result.failure(Exception("error"))

        val putPreferenceResult =
            encryptedPreferences.putPreference(PREFERENCE_KEY, PREFERENCE_VALUE, String.serializer())

        assertFalse(putPreferenceResult)
        assertNull(encryptedPreferences.preference(PREFERENCE_KEY, String.serializer()))
    }

    private fun encryptedPreferences(): EncryptedPreferences =
        AndroidEncryptedPreferences(
            preferencesFileProvider = {
                val fileName = EncryptedPreferences.preferencesFileName(generateRandomString(5))
                temporalFolder.newFile(fileName)
            },
            dispatchersProvider = FakeDispatchersProvider(Dispatchers.Unconfined),
            encryptor = fakeEncryptor,
            appLogger = appLogger(),
        )

    companion object {
        const val PREFERENCE_KEY = "MyPreference"
        const val PREFERENCE_VALUE = "MyPreferenceValue"
    }
}