package oneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import oneclick.server.services.app.plugins.authentication.AuthenticationType
import oneclick.server.shared.authentication.security.UserJwtProvider
import oneclick.shared.contracts.auth.models.Jwt

internal fun Application.configureSessions() {
    install(Sessions) {
        cookie<Jwt>(AuthenticationType.USER_SESSION.value) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = UserJwtProvider.JWT_EXPIRATION_TIME
            cookie.secure = true
            cookie.httpOnly = true
        }
    }
}
