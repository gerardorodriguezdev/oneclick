package oneclick.client.shared.network.dataSources

import kotlinx.serialization.builtins.serializer

interface TokenDataSource {
    suspend fun token(): String?
    suspend fun set(token: String): Boolean
    suspend fun clear(): Boolean
}

internal class MemoryTokenDataSource : TokenDataSource {
    private var token: String? = null

    override suspend fun token(): String? = token
    override suspend fun set(token: String): Boolean {
        this.token = token
        return true
    }

    override suspend fun clear(): Boolean {
        token = null
        return true
    }
}

class LocalTokenDataSource(
    private val preferences: Preferences,
) : TokenDataSource {
    override suspend fun token(): String? =
        preferences.preference(key = TOKEN_KEY, serializer = String.serializer())

    override suspend fun clear(): Boolean = preferences.clearPreference(TOKEN_KEY)

    override suspend fun set(token: String): Boolean =
        preferences.putPreference(key = TOKEN_KEY, value = token, serializer = String.serializer())

    private companion object {
        const val TOKEN_KEY = "token"
    }
}
