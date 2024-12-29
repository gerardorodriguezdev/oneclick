package theoneclick.shared.core.extensions

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

fun defaultHttpClient(
    engine: HttpClientEngine,
    protocol: URLProtocol?,
    host: String?,
    port: Int?,
): HttpClient =
    HttpClient(engine) {
        install(ContentNegotiation) {
            json()
        }

        install(DefaultRequest) {
            protocol?.let {
                url.protocol = protocol
            }

            host?.let {
                this.host = host
            }

            port?.let {
                this.port = port
            }
        }
    }
