package theoneclick.client.core.plugins

import io.ktor.client.plugins.api.*
import io.ktor.http.*
import theoneclick.client.core.dataSources.EmptyTokenDataSource
import theoneclick.client.core.dataSources.TokenDataSource

val TokenManager = createClientPlugin("TokenManager", ::TokenManagerConfiguration) {
    val tokenProvider = pluginConfig.tokenDataSource

    onRequest { request, _ ->
        val token = tokenProvider.token()
        token?.let {
            request.headers.append(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}

class TokenManagerConfiguration(
    var tokenDataSource: TokenDataSource = EmptyTokenDataSource(),
)