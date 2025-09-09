package theoneclick.client.shared.network.plugins

import io.ktor.client.plugins.api.*
import io.ktor.client.statement.*
import io.ktor.http.*
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint

val LogoutProxy = createClientPlugin("LogoutProxy", ::LogoutProxyConfiguration) {
    val onLogout = pluginConfig.onLogout

    onResponse { response ->
        if (response.status == HttpStatusCode.Unauthorized) {
            onLogout()
        }
    }

    onResponse { response ->
        if (response.request.url.fullPath == ClientEndpoint.LOGOUT && response.status == HttpStatusCode.OK) {
            onLogout()
        }
    }
}

class LogoutProxyConfiguration(
    var onLogout: suspend () -> Unit = {},
)
