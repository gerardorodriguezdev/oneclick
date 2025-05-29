package theoneclick.client.app.plugins

import io.ktor.client.plugins.api.*
import io.ktor.client.statement.*
import io.ktor.http.*
import theoneclick.shared.core.models.endpoints.ClientEndpoint

val LogoutProxy = createClientPlugin("LogoutProxy", ::LogoutManagerConfiguration) {
    val onLogout = pluginConfig.onLogout

    onResponse { response ->
        if (response.status == HttpStatusCode.Unauthorized) {
            onLogout()
        }
    }

    onResponse { response ->
        if (response.request.url.fullPath == ClientEndpoint.LOGOUT.route && response.status == HttpStatusCode.OK) {
            onLogout()
        }
    }
}

class LogoutManagerConfiguration(
    var onLogout: suspend () -> Unit = {},
)
