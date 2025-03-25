package theoneclick.client.core.dataSources

import kotlinx.coroutines.delay

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

class AndroidLocalTokenDataSource : TokenDataSource {
    override suspend fun token(): String? {
        delay(5000)
        return null
    }

    override suspend fun clear() {}

    override suspend fun set(token: String) {}
}