package theoneclick.server.services.app.di

import io.ktor.server.application.Application
import io.ktor.util.logging.Logger
import theoneclick.server.services.app.repositories.HomesRepository
import theoneclick.server.services.app.repositories.UsersRepository
import theoneclick.server.shared.auth.security.Encryptor
import theoneclick.server.shared.auth.security.JwtProvider
import theoneclick.server.shared.auth.security.UuidProvider
import theoneclick.shared.timeProvider.TimeProvider

class Dependencies(
    protocol: String,
    host: String,
    val disableRateLimit: Boolean,
    val encryptor: Encryptor,
    val timeProvider: TimeProvider,
    val logger: Logger,
    val jwtProvider: JwtProvider,
    val usersRepository: UsersRepository,
    val uuidProvider: UuidProvider,
    val homesRepository: HomesRepository,
    val onShutdown: (application: Application) -> Unit,
) {
    val baseUrl: String = "$protocol://$host"
}