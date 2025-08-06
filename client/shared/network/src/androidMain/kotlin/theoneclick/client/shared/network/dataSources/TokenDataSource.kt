package theoneclick.client.shared.network.dataSources

import kotlinx.serialization.builtins.serializer

interface TokenDataSource {
    suspend fun token(): String?
    suspend fun set(token: String): Boolean
    suspend fun clear(): Boolean
}

internal class AndroidMemoryTokenDataSource : TokenDataSource {
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

class AndroidLocalTokenDataSource(
    private val encryptedPreferences: EncryptedPreferences,
) : TokenDataSource {
    override suspend fun token(): String? =
        encryptedPreferences.preference(TOKEN_KEY, String.serializer())

    override suspend fun clear(): Boolean = encryptedPreferences.clearPreference(TOKEN_KEY)

    override suspend fun set(token: String): Boolean =
        encryptedPreferences.putPreference(TOKEN_KEY, token, String.serializer())

    private companion object {
        const val TOKEN_KEY = "token"
    }
}
