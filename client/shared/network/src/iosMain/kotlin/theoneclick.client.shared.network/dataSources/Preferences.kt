package theoneclick.client.shared.network.dataSources

import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import okio.Path
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.logging.AppLogger

interface Preferences {
    suspend fun <T> preference(key: String, serializer: KSerializer<T>): T?
    suspend fun <T> putPreference(key: String, value: T, serializer: KSerializer<T>): Boolean
    suspend fun clearPreference(key: String): Boolean
}

class IOSPreferences(
    preferencesFileProvider: () -> Path,
    dispatchersProvider: DispatchersProvider,
    private val appLogger: AppLogger,
) : Preferences {
    private val dataStore = PreferenceDataStoreFactory.createWithPath(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        migrations = emptyList(),
        scope = CoroutineScope(dispatchersProvider.io()),
        produceFile = preferencesFileProvider,
    )

    override suspend fun <T> preference(key: String, serializer: KSerializer<T>): T? {
        val value = dataStore
            .data
            .map { preferences ->
                val key = stringPreferencesKey(key)
                val value = preferences[key] ?: return@map null
                val decodedValue = Json.decodeFromString(serializer, value)
                decodedValue
            }
            .catch { error ->
                appLogger.e("Exception '${error.stackTraceToString()}' while getting preference key '$key'")

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
                    val key = stringPreferencesKey(key)
                    preferences[key] = valueString
                }

            true
        } catch (error: Throwable) {
            appLogger.e("Exception '${error.stackTraceToString()}' while putting preference key '$key'")

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
        } catch (error: Throwable) {
            appLogger.e("Exception '${error.stackTraceToString()}' while clearing key '$key'")
            false
        }
}
