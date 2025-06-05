package theoneclick.client.shared.network.platform

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import theoneclick.client.shared.network.plugins.LogoutProxy
import theoneclick.shared.core.models.agents.Agent

fun wasmHttpClient(
    httpClientEngine: HttpClientEngine,
    logoutManager: LogoutManager,
): HttpClient {
    return HttpClient(httpClientEngine) {
        install(ContentNegotiation) {
            json()
        }

        install(DefaultRequest) {
            contentType(ContentType.Application.Json)

            userAgent(Agent.BROWSER.value)
        }

        install(LogoutProxy) {
            onLogout = logoutManager::logout
        }
    }
}
