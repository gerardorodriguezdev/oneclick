package theoneclick.server.services.auth.di

import io.ktor.util.logging.*
import theoneclick.server.shared.auth.security.Encryptor
import theoneclick.server.shared.auth.security.JwtProvider
import theoneclick.server.shared.core.di.Dependencies
import theoneclick.shared.timeProvider.TimeProvider

class AppComponent(
    protocol: String,
    host: String,
    override val disableRateLimit: Boolean,
    override val encryptor: Encryptor,
    override val timeProvider: TimeProvider,
    override val logger: Logger,
    override val jwtProvider: JwtProvider,
    override val healthzPath: String,
) : Dependencies {
    override val baseUrl: String = "$protocol://$host"
}