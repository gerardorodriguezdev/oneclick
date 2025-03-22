package theoneclick.client.core.platform

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import theoneclick.client.core.buildkonfig.BuildKonfig
import theoneclick.shared.core.models.agents.Agent

fun wasmHttpClient(urlProtocol: URLProtocol?): HttpClient {
    val engine = Js.create()

    return HttpClient(engine) {
        install(ContentNegotiation) {
            json()
        }

        install(DefaultRequest) {
            urlProtocol?.let {
                url.protocol = urlProtocol
            }

            BuildKonfig.HOST?.let {
                this.host = host
            }

            BuildKonfig.PORT?.let {
                this.port = port
            }

            userAgent(Agent.BROWSER.value)
        }
    }
}