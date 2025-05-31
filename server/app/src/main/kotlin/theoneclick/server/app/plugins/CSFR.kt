package theoneclick.server.app.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.csrf.*

fun Application.configureCSFR() {
    install(CSRF) {
        allowOrigin("http://localhost:8080")
        originMatchesHost()
        checkHeader("X-CSRF-Token")
    }
}