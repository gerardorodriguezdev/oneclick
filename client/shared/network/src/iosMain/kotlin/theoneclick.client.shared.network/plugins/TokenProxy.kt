package oneclick.client.shared.network.plugins

import io.ktor.client.plugins.api.*
import io.ktor.http.*
import oneclick.client.shared.network.dataSources.IOSMemoryTokenDataSource
import oneclick.client.shared.network.dataSources.TokenDataSource

internal val TokenProxy = createClientPlugin("TokenProxy", ::TokenManagerConfiguration) {
    val tokenDataSource = pluginConfig.tokenDataSource

    onRequest { request, _ ->
        val token = tokenDataSource.token()
        token?.let {
            request.headers.append(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}

internal class TokenManagerConfiguration(
    var tokenDataSource: TokenDataSource = IOSMemoryTokenDataSource(),
)
