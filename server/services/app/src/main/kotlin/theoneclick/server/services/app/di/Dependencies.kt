package oneclick.server.services.app.di

import io.ktor.server.application.Application
import io.ktor.util.logging.Logger
import oneclick.server.services.app.dataSources.base.InvalidJwtDataSource
import oneclick.server.services.app.repositories.HomesRepository
import oneclick.server.services.app.repositories.UsersRepository
import oneclick.server.shared.auth.security.Encryptor
import oneclick.server.shared.auth.security.JwtProvider
import oneclick.server.shared.auth.security.UuidProvider
import oneclick.shared.timeProvider.TimeProvider

internal class Dependencies(
    protocol: String,
    host: String,
    val disableRateLimit: Boolean,
    val encryptor: Encryptor,
    val timeProvider: TimeProvider,
    val logger: Logger,
    val jwtProvider: JwtProvider,
    val invalidJwtDataSource: InvalidJwtDataSource,
    val usersRepository: UsersRepository,
    val uuidProvider: UuidProvider,
    val homesRepository: HomesRepository,
    val onShutdown: (application: Application) -> Unit,
) {
    val baseUrl: String = "$protocol://$host"
}