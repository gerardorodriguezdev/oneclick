package theoneclick.server.shared.core.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import theoneclick.server.shared.auth.security.JwtProvider
import theoneclick.shared.contracts.auth.models.Jwt

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
