package theoneclick.client.core.dataSources

import kotlinx.coroutines.delay

interface TokenDataSource {
    suspend fun token(): String?
    suspend fun set(token: String)
    suspend fun clear()
}

class EmptyTokenDataSource : TokenDataSource {
    override suspend fun token(): String? = null
    override suspend fun set(token: String) {}
    override suspend fun clear() {}
}

class AndroidLocalTokenDataSource : TokenDataSource {
    override suspend fun token(): String? {
        delay(5000)
        return null
    }

    override suspend fun clear() {}

    override suspend fun set(token: String) {}
}