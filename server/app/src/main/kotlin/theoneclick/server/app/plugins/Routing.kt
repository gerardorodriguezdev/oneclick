package theoneclick.server.app.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import theoneclick.server.app.endpoints.*
import theoneclick.server.shared.di.Environment
import theoneclick.server.shared.repositories.HomesRepository
import theoneclick.server.shared.repositories.SessionsRepository
import theoneclick.server.shared.repositories.UsersRepository
import theoneclick.server.shared.security.Encryptor
import theoneclick.server.shared.security.UuidProvider

fun Application.configureRouting(
    environment: Environment,
    usersRepository: UsersRepository,
    homesRepository: HomesRepository,
    sessionsRepository: SessionsRepository,
    encryptor: Encryptor,
    uuidProvider: UuidProvider,
) {
    routing {
        healthzEndpoint()
        requestLoginEndpoint(usersRepository, sessionsRepository, encryptor, uuidProvider)
        isUserLoggedEndpoint()
        logoutEndpoint(sessionsRepository)
        homesListEndpoint(sessionsRepository, homesRepository)

        if (environment.enableQAAPI) {
            qaapi(usersRepository)
        }
    }
}
