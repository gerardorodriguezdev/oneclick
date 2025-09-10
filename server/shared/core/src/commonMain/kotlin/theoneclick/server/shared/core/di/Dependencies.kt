package theoneclick.server.shared.core.di

import io.ktor.util.logging.*
import theoneclick.server.shared.auth.security.Encryptor
import theoneclick.server.shared.auth.security.JwtProvider
import theoneclick.shared.timeProvider.TimeProvider

interface Dependencies {
    val disableRateLimit: Boolean
    val baseUrl: String
    val encryptor: Encryptor
    val timeProvider: TimeProvider
    val logger: Logger
    val jwtProvider: JwtProvider
    val healthzPath: String
}