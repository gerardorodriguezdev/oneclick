package oneclick.client.shared.network.dataSources

import kotlinx.serialization.KSerializer

interface Preferences {
    suspend fun <T> preference(key: String, serializer: KSerializer<T>): T?
    suspend fun <T> putPreference(key: String, value: T, serializer: KSerializer<T>): Boolean
    suspend fun clearPreference(key: String): Boolean
}