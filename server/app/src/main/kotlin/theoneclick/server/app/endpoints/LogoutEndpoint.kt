package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.shared.extensions.defaultAuthentication
import theoneclick.shared.contracts.core.models.Jwt
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint

fun Routing.logoutEndpoint() {
    defaultAuthentication {
        get(ClientEndpoint.LOGOUT.route) {
            call.sessions.clear<Jwt>()
            call.respond(HttpStatusCode.OK)
        }
    }
}
