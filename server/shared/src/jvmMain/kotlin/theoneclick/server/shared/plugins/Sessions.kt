package theoneclick.server.shared.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import theoneclick.server.shared.plugins.authentication.AuthenticationConstants
import theoneclick.shared.contracts.core.models.Jwt

fun Application.configureSessions() {
    install(Sessions) {
        cookie<Jwt>(AuthenticationConstants.JWT_SESSION_NAME) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = AuthenticationConstants.JWT_EXPIRATION_IN_MILLIS
            cookie.secure = true
            cookie.httpOnly = true
        }
    }
}