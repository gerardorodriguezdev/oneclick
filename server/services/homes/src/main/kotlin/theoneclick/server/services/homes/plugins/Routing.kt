package theoneclick.server.services.homes.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import theoneclick.server.services.homes.endpoints.healthzEndpoint
import theoneclick.server.services.homes.endpoints.homesListEndpoint
import theoneclick.server.shared.di.Environment
import theoneclick.server.services.homes.repositories.HomesRepository
import theoneclick.server.shared.security.Encryptor
import theoneclick.server.shared.security.UuidProvider

fun Application.configureRouting(
    environment: Environment,
    homesRepository: HomesRepository,
    encryptor: Encryptor,
    uuidProvider: UuidProvider,
) {
    routing {
        healthzEndpoint()
        homesListEndpoint(homesRepository)
    }
}
