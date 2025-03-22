package theoneclick.client.core.platform

import io.ktor.client.plugins.api.*
import io.ktor.http.*

val Token = createClientPlugin("Token", ::TokenConfiguration) {
    val tokenProvider = pluginConfig.tokenProvider

    onRequest { request, _ ->
        val token = tokenProvider.token()
        token?.let {
            request.headers.append(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}

class TokenConfiguration(
    var tokenProvider: TokenProvider = InMemoryTokenProvider(),
)