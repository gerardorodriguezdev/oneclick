package theoneclick.client.core.platform

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import theoneclick.shared.core.models.agents.Agent
import theoneclick.shared.timeProvider.TimeProvider

fun androidHttpClient(
    urlProtocol: URLProtocol?,
    host: String?,
    port: Int?,
    timeProvider: TimeProvider,
    tokenProvider: TokenProvider,
): HttpClient {
    val engine = androidHttpClientEngine(timeProvider = timeProvider)
    return HttpClient(engine) {
        install(ContentNegotiation) {
            json()
        }

        install(DefaultRequest) {
            urlProtocol?.let {
                url.protocol = urlProtocol
            }

            host?.let {
                this.host = host
            }

            port?.let {
                this.port = port
            }

            userAgent(Agent.MOBILE.value)
        }

        install(Token) {
            this.tokenProvider = tokenProvider
        }
    }
}
