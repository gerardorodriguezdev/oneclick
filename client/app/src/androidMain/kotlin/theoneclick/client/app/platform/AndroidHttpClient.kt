package theoneclick.client.app.platform

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import theoneclick.client.app.buildkonfig.BuildKonfig
import theoneclick.client.app.dataSources.TokenDataSource
import theoneclick.client.app.mappers.urlProtocol
import theoneclick.client.app.plugins.LogoutProxy
import theoneclick.client.app.plugins.TokenProxy
import theoneclick.shared.core.models.agents.Agent
import theoneclick.shared.core.platform.AppLogger

fun androidHttpClient(
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

            val urlProtocol = BuildKonfig.urlProtocol()
            urlProtocol?.let {
                url.protocol = urlProtocol
            }

            BuildKonfig.HOST?.let { host ->
                this.host = host
            }

            BuildKonfig.PORT?.let { port ->
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
    }

private fun AppLogger.toLogger(): Logger =
    object : Logger {
        override fun log(message: String) {
            i("AppNetworking", message)
        }
    }
