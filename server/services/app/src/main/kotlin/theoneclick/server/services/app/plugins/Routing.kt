package theoneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import theoneclick.server.services.app.endpoints.*
import theoneclick.server.services.app.repositories.HomesRepository
import theoneclick.server.services.app.repositories.UsersRepository
import theoneclick.server.shared.auth.security.Encryptor
import theoneclick.server.shared.auth.security.JwtProvider
import theoneclick.server.shared.auth.security.UuidProvider

internal fun Application.configureRouting(
    usersRepository: UsersRepository,
    encryptor: Encryptor,
    jwtProvider: JwtProvider,
    uuidProvider: UuidProvider,
    homesRepository: HomesRepository,
) {
    routing {
        healthzEndpoint()
        isUserLoggedEndpoint()
        logoutEndpoint()
        requestLoginEndpoint(
            usersRepository = usersRepository,
            encryptor = encryptor,
            jwtProvider = jwtProvider,
            uuidProvider = uuidProvider,
        )
        homesListEndpoint(homesRepository = homesRepository)
    }
}