package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.shared.extensions.defaultAuthentication
import theoneclick.server.shared.extensions.requireToken
import theoneclick.server.shared.repositories.SessionsRepository
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint

fun Routing.logoutEndpoint(sessionsRepository: SessionsRepository) {
    defaultAuthentication {
        get(ClientEndpoint.LOGOUT.route) {
            val token = requireToken()
            sessionsRepository.deleteSession(token)
            call.respond(HttpStatusCode.OK)
        }
    }
}
