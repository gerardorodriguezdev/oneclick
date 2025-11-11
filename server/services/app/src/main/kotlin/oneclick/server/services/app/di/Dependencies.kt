package oneclick.server.services.app.di

import io.ktor.server.application.*
import io.ktor.util.logging.*
import oneclick.server.services.app.dataSources.base.InvalidJwtDataSource
import oneclick.server.services.app.repositories.HomesRepository
import oneclick.server.services.app.repositories.UsersRepository
import oneclick.server.shared.authentication.security.HomeJwtProvider
import oneclick.server.shared.authentication.security.PasswordManager
import oneclick.server.shared.authentication.security.UserJwtProvider
import oneclick.server.shared.authentication.security.UuidProvider
import oneclick.shared.timeProvider.TimeProvider

internal class Dependencies(
    protocol: String,
    host: String,
    val disableRateLimit: Boolean,
    val passwordManager: PasswordManager,
    val timeProvider: TimeProvider,
    val logger: Logger,
    val userJwtProvider: UserJwtProvider,
    val homeJwtProvider: HomeJwtProvider,
    val invalidJwtDataSource: InvalidJwtDataSource,
    val usersRepository: UsersRepository,
    val uuidProvider: UuidProvider,
    val homesRepository: HomesRepository,
    val onShutdown: (application: Application) -> Unit,
) {
    val baseUrl: String = "$protocol://$host"
}