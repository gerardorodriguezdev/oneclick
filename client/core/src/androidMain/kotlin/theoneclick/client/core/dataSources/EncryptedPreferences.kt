package theoneclick.client.core.dataSources

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import theoneclick.client.core.security.Encryptor
import theoneclick.shared.dispatchers.platform.DispatchersProvider

interface EncryptedPreferences {
    suspend fun <T> preference(key: String, serializer: KSerializer<T>): T?
    suspend fun <T> putPreference(key: String, value: T, serializer: KSerializer<T>): Boolean
    suspend fun clearPreference(key: String): Boolean
}

//TODO: Test
//TODO: Remove charsets?
class AndroidEncryptedPreferences(
    context: Context,
    dispatchersProvider: DispatchersProvider,
    private val encryptor: Encryptor,
) : EncryptedPreferences {
    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(
        name = PREFERENCES_NAME,
        corruptionHandler = ReplaceFileCorruptionHandler<Preferences> { emptyPreferences() },
        scope = CoroutineScope(dispatchersProvider.io()),
    )
    private val dataStore = context.datastore

    override suspend fun <T> preference(key: String, serializer: KSerializer<T>): T? {
        val value = dataStore
            .data
            .map { preferences ->
                val key = stringPreferencesKey(key)
                val value = preferences[key] ?: return@map null
                val valueByteArray = value.toByteArray(Charsets.UTF_8)
                val decryptedValue = encryptor.decrypt(valueByteArray)
                val decryptedValueString = decryptedValue?.toString(Charsets.UTF_8) ?: return@map null
                Json.decodeFromString(serializer, decryptedValueString)
            }
            .catch { cause ->
                clearPreference(key)
                emit(null)
            }
            .first()

        return value
    }

    override suspend fun <T> putPreference(key: String, value: T, serializer: KSerializer<T>): Boolean =
        try {
            dataStore
                .edit { preferences ->
                    val valueString = Json.encodeToString(serializer, value)
                    val valueByteArray = valueString.toByteArray(Charsets.UTF_8)
                    val encryptedValue = encryptor.encrypt(valueByteArray)
                    val key = stringPreferencesKey(key)
                    preferences[key] = encryptedValue.toString(Charsets.UTF_8)
                }

            true
        } catch (_: Throwable) {
            clearPreference(key)
            false
        }

    override suspend fun clearPreference(key: String): Boolean =
        try {
            dataStore
                .edit { mutablePreference ->
                    val key = stringPreferencesKey(key)
                    mutablePreference.remove(key)
                }

            true
        } catch (_: Throwable) {
            false
        }

    private companion object {
        const val PREFERENCES_NAME = "settings"
    }
}