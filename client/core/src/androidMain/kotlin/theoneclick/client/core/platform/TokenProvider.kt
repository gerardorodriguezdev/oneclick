package theoneclick.client.core.platform

import kotlinx.coroutines.delay

interface TokenProvider {
    suspend fun token(): String?
}

class InMemoryTokenProvider : TokenProvider {
    var token: String? = null

    override suspend fun token(): String? = token
}

class DiskTokenProvider : TokenProvider {
    override suspend fun token(): String? {
        delay(5000)
        return null
    }
}