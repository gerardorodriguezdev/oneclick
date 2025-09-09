package theoneclick.server.services.auth.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import theoneclick.server.services.auth.endpoints.healthzEndpoint
import theoneclick.server.services.auth.endpoints.isUserLoggedEndpoint
import theoneclick.server.services.auth.endpoints.logoutEndpoint
import theoneclick.server.services.auth.endpoints.requestLoginEndpoint
import theoneclick.server.services.auth.repositories.UsersRepository
import theoneclick.server.shared.security.Encryptor
import theoneclick.server.shared.security.UuidProvider

fun Application.configureRouting(
    usersRepository: UsersRepository,
    encryptor: Encryptor,
    uuidProvider: UuidProvider,
) {
    routing {
        healthzEndpoint()
        requestLoginEndpoint(usersRepository, encryptor, uuidProvider)
        isUserLoggedEndpoint()
        logoutEndpoint()
    }
}
