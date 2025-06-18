package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.app.repositories.UsersRepository
import theoneclick.server.app.extensions.defaultAuthentication
import theoneclick.server.app.extensions.requireToken
import theoneclick.shared.contracts.core.dtos.responses.HomesResponseDto
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint

fun Routing.homesListEndpoint(usersRepository: UsersRepository) {
    defaultAuthentication {
        get(ClientEndpoint.HOMES.route) {
            val token = requireToken()
            val user = usersRepository.user(token)
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest)
            } else {
                call.respond(HomesResponseDto(homes = user.homes))
            }
        }
    }
}
