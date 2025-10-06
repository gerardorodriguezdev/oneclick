package oneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import oneclick.server.services.app.dataSources.base.InvalidJwtDataSource
import oneclick.server.services.app.endpoints.*
import oneclick.server.services.app.repositories.HomesRepository
import oneclick.server.services.app.repositories.UsersRepository
import oneclick.server.shared.auth.security.Encryptor
import oneclick.server.shared.auth.security.JwtProvider
import oneclick.server.shared.auth.security.UuidProvider

internal fun Application.configureRouting(
    usersRepository: UsersRepository,
    encryptor: Encryptor,
    jwtProvider: JwtProvider,
    uuidProvider: UuidProvider,
    homesRepository: HomesRepository,
    invalidJwtDataSource: InvalidJwtDataSource,
) {
    routing {
        healthzEndpoint()
        isUserLoggedEndpoint()
        logoutEndpoint(invalidJwtDataSource = invalidJwtDataSource)
        requestLoginEndpoint(
            usersRepository = usersRepository,
            encryptor = encryptor,
            jwtProvider = jwtProvider,
            uuidProvider = uuidProvider,
        )
        homesListEndpoint(homesRepository = homesRepository)
        appEndpoint()
    }
}