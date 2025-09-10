package theoneclick.server.services.homes.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import theoneclick.server.services.homes.endpoints.homesListEndpoint
import theoneclick.server.services.homes.repositories.HomesRepository

fun Application.configureRouting(homesRepository: HomesRepository) {
    routing {
        homesListEndpoint(homesRepository)
    }
}
