package theoneclick.client.core.platform

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import theoneclick.shared.core.models.agents.Agent

//TODO: Repeated
fun wasmHttpClient(
    urlProtocol: URLProtocol?,
    host: String?,
    port: Int?,
): HttpClient {
    val engine = Js.create()

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

            userAgent(Agent.BROWSER.value)
        }
    }
}