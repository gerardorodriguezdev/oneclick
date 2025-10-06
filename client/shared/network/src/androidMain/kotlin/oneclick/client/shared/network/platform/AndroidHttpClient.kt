package oneclick.client.shared.network.platform

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import oneclick.client.shared.network.dataSources.TokenDataSource
import oneclick.client.shared.network.plugins.LogoutProxy
import oneclick.client.shared.network.plugins.TokenProxy
import oneclick.shared.contracts.core.models.agents.Agent
import oneclick.shared.logging.AppLogger

fun androidHttpClient(
    urlProtocol: URLProtocol?,
    host: String?,
    port: Int?,
    appLogger: AppLogger,
    httpClientEngine: HttpClientEngine,
    tokenDataSource: TokenDataSource,
    logoutManager: LogoutManager,
): HttpClient =
    HttpClient(httpClientEngine) {
        install(ContentNegotiation) {
            json()
        }

        install(DefaultRequest) {
            contentType(ContentType.Application.Json)

            urlProtocol?.let {
                url.protocol = urlProtocol
            }

            host?.let { host ->
                this.host = host
            }

            port?.let { port ->
                this.port = port
            }

            userAgent(Agent.MOBILE.value)
        }

        install(TokenProxy) {
            this.tokenDataSource = tokenDataSource
        }

        install(LogoutProxy) {
            onLogout = logoutManager::logout
        }

        install(Logging) {
            logger = appLogger.toLogger()
            level = LogLevel.ALL
        }

        install(ContentEncoding) {
            gzip()
        }
    }

private fun AppLogger.toLogger(): Logger =
    object : Logger {
        override fun log(message: String) {
            i("AppNetworking", message)
        }
    }
