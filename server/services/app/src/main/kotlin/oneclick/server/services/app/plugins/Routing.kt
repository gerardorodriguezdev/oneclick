package oneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import oneclick.server.services.app.dataSources.base.InvalidJwtDataSource
import oneclick.server.services.app.endpoints.*
import oneclick.server.services.app.repositories.HomesRepository
import oneclick.server.services.app.repositories.UsersRepository
import oneclick.server.shared.authentication.security.HomeJwtProvider
import oneclick.server.shared.authentication.security.PasswordManager
import oneclick.server.shared.authentication.security.UserJwtProvider
import oneclick.server.shared.authentication.security.UuidProvider

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
        homeRequestLoginEndpoint(
            usersRepository = usersRepository,
            passwordManager = passwordManager,
            homeJwtProvider = homeJwtProvider,
            homesRepository = homesRepository,
        )
        userHomesEndpoint(homesRepository = homesRepository)
        homeSyncDevicesEndpoint(homesRepository = homesRepository)
        appEndpoint()
    }
}