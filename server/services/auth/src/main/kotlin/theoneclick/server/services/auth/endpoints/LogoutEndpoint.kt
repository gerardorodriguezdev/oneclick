package theoneclick.server.services.auth.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.shared.core.extensions.defaultAuthentication
import theoneclick.shared.contracts.auth.models.Jwt
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint

fun Routing.logoutEndpoint() {
    defaultAuthentication {
        get(ClientEndpoint.LOGOUT) {
            call.sessions.clear<Jwt>()
            call.respond(HttpStatusCode.OK)
        }
    }
}
