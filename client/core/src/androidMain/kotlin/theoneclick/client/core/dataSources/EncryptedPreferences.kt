package theoneclick.client.core.dataSources

import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import theoneclick.client.core.security.Encryptor
import theoneclick.shared.core.platform.AppLogger
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import java.io.File

interface EncryptedPreferences {
    suspend fun <T> preference(key: String, serializer: KSerializer<T>): T?
    suspend fun <T> putPreference(key: String, value: T, serializer: KSerializer<T>): Boolean
    suspend fun clearPreference(key: String): Boolean

    companion object {
        fun preferencesFileName(fileName: String): String = "$fileName.preferences_pb"
    }
}

class AndroidEncryptedPreferences(
    preferencesFileProvider: () -> File,
    dispatchersProvider: DispatchersProvider,
    private val encryptor: Encryptor,
    private val appLogger: AppLogger,
) : EncryptedPreferences {
    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = preferencesFileProvider,
        corruptionHandler = ReplaceFileCorruptionHandler<Preferences> { emptyPreferences() },
        scope = CoroutineScope(dispatchersProvider.io()),
    )

    override suspend fun <T> preference(key: String, serializer: KSerializer<T>): T? {
        appLogger.i("Getting preference key '$key'")

        val value = dataStore
            .data
            .map { preferences ->
                val key = stringPreferencesKey(key)
                val value = preferences[key]

                if (value == null) {
                    appLogger.i("Value for preference key '$key' is null")
                    return@map null
                }

                val valueByteArray = value.toByteArray(Charsets.UTF_8)
                val decryptedValue = encryptor.decrypt(valueByteArray).getOrThrow()
                val decryptedValueString = decryptedValue?.toString(Charsets.UTF_8) ?: return@map null
                val decodedValue = Json.decodeFromString(serializer, decryptedValueString)

                appLogger.i("Returning value '$decodedValue' for key '$key'")
                decodedValue
            }
            .catch { error ->
                appLogger.e("Exception '${error.stackTraceToString()}' when getting preference key '$key'")

                clearPreference(key)
                emit(null)
            }
            .first()

        return value
    }

    override suspend fun <T> putPreference(key: String, value: T, serializer: KSerializer<T>): Boolean {
        return try {
            appLogger.i("Putting preference key '$key' of value '$value'")

            dataStore
                .edit { preferences ->
                    val valueString = Json.encodeToString(serializer, value)
                    val valueByteArray = valueString.toByteArray(Charsets.UTF_8)
                    val encryptedValue = encryptor.encrypt(valueByteArray).getOrThrow()
                    val key = stringPreferencesKey(key)
                    preferences[key] = encryptedValue.toString(Charsets.UTF_8)
                }

            true
        } catch (error: Throwable) {
            appLogger.e("Exception '${error.stackTraceToString()}' when putting preference key '$key'")

            clearPreference(key)
            false
        }
    }

    override suspend fun clearPreference(key: String): Boolean =
        try {
            appLogger.i("Clearing preference key '$key'")

            dataStore
                .edit { mutablePreference ->
                    val key = stringPreferencesKey(key)
                    mutablePreference.remove(key)
                }

            true
        } catch (error: Throwable) {
            appLogger.e("Exception '${error.stackTraceToString()}' when clearing key '$key'")
            false
        }
}
