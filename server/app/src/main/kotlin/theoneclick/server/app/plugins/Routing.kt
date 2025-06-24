package theoneclick.server.app.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import theoneclick.server.app.di.Environment
import theoneclick.server.app.endpoints.*
import theoneclick.server.app.repositories.HomesRepository
import theoneclick.server.app.repositories.UsersRepository
import theoneclick.server.shared.security.Encryptor
import theoneclick.server.shared.security.UuidProvider

fun Application.configureRouting(
    environment: Environment,
    usersRepository: UsersRepository,
    homesRepository: HomesRepository,
    encryptor: Encryptor,
    uuidProvider: UuidProvider,
) {
    routing {
        healthzEndpoint()
        requestLoginEndpoint(usersRepository, encryptor, uuidProvider)
        isUserLoggedEndpoint()
        logoutEndpoint(usersRepository)
        homesListEndpoint(usersRepository, homesRepository)

        if (environment.enableQAAPI) {
            qaapi(usersRepository)
        }
    }
}
