package theoneclick.client.core.plugins

import io.ktor.client.plugins.api.*
import io.ktor.http.*
import theoneclick.client.core.dataSources.AndroidInMemoryTokenDataSource
import theoneclick.client.core.dataSources.TokenDataSource

val TokenProxy = createClientPlugin("TokenProxy", ::TokenManagerConfiguration) {
    val tokenDataSource = pluginConfig.tokenDataSource

    onRequest { request, _ ->
        val token = tokenDataSource.token()
        token?.let {
            request.headers.append(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}

class TokenManagerConfiguration(
    var tokenDataSource: TokenDataSource = AndroidInMemoryTokenDataSource(),
)
