package theoneclick.client.core.testing.dataSources

import theoneclick.client.core.dataSources.TokenDataSource

class FakeTokenDataSource(
    var tokenResult: String? = null,
) : TokenDataSource {
    override suspend fun token(): String? = tokenResult

    override suspend fun set(token: String) {
        tokenResult = token
    }

    override suspend fun clear() {
        tokenResult = null
    }
}