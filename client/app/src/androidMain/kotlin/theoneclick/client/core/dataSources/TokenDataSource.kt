package theoneclick.client.core.dataSources

import kotlinx.serialization.builtins.serializer

interface TokenDataSource {
    suspend fun token(): String?
    suspend fun set(token: String)
    suspend fun clear()
}

class AndroidInMemoryTokenDataSource : TokenDataSource {
    private var token: String? = null

    override suspend fun token(): String? = token
    override suspend fun set(token: String) {
        this.token = token
    }

    override suspend fun clear() {
        token = null
    }
}

class AndroidLocalTokenDataSource(
    private val encryptedPreferences: EncryptedPreferences,
) : TokenDataSource {
    override suspend fun token(): String? =
        encryptedPreferences.preference<String>(TOKEN_KEY, String.serializer())

    override suspend fun clear() {
        encryptedPreferences.clearPreference(TOKEN_KEY)
    }

    override suspend fun set(token: String) {
        encryptedPreferences.putPreference(TOKEN_KEY, token, String.serializer())
    }

    private companion object {
        const val TOKEN_KEY = "token"
    }
}
