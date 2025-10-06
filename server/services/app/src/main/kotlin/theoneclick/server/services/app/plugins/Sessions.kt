package oneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import oneclick.server.shared.auth.security.JwtProvider
import oneclick.shared.contracts.auth.models.Jwt

internal fun Application.configureSessions(jwtProvider: JwtProvider) {
    install(Sessions) {
        cookie<Jwt>(jwtProvider.jwtSessionName) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = jwtProvider.jwtExpirationTimeInMillis
            cookie.secure = true
            cookie.httpOnly = true
        }
    }
}
