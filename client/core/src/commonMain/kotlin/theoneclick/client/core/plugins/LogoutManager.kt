package theoneclick.client.core.plugins

import io.ktor.client.plugins.api.*
import io.ktor.http.*

val LogoutManager = createClientPlugin("LogoutManager", ::LogoutManagerConfiguration) {
    val onLogout = pluginConfig.onLogout

    onResponse { response ->
        if (response.status == HttpStatusCode.Unauthorized) {
            onLogout()
        }
    }
}

class LogoutManagerConfiguration(
    var onLogout: suspend () -> Unit = {},
)