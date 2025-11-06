package oneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import oneclick.server.services.app.dataSources.base.InvalidJwtDataSource
import oneclick.server.services.app.endpoints.*
import oneclick.server.services.app.repositories.HomesRepository
import oneclick.server.services.app.repositories.UsersRepository
import oneclick.server.shared.auth.security.HomeJwtProvider
import oneclick.server.shared.auth.security.PasswordManager
import oneclick.server.shared.auth.security.UserJwtProvider
import oneclick.server.shared.auth.security.UuidProvider

internal fun Application.configureRouting(
    usersRepository: UsersRepository,
    passwordManager: PasswordManager,
    userJwtProvider: UserJwtProvider,
    homeJwtProvider: HomeJwtProvider,
    uuidProvider: UuidProvider,
    homesRepository: HomesRepository,
    invalidJwtDataSource: InvalidJwtDataSource,
) {
    routing {
        healthzEndpoint()
        isLoggedEndpoint()
        logoutEndpoint(invalidJwtDataSource = invalidJwtDataSource)
        userRequestLoginEndpoint(
            usersRepository = usersRepository,
            passwordManager = passwordManager,
            userJwtProvider = userJwtProvider,
            uuidProvider = uuidProvider,
        )
        userHomesEndpoint(homesRepository = homesRepository)
        syncDevicesEndpoint(homesRepository = homesRepository)
        appEndpoint()
    }
}