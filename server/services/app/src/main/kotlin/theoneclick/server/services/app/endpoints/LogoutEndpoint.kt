package theoneclick.server.services.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.services.app.plugins.authentication.defaultAuthentication
import theoneclick.shared.contracts.auth.models.Jwt
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint

internal fun Routing.logoutEndpoint() {
    defaultAuthentication {
        get(ClientEndpoint.LOGOUT.route) {
            call.sessions.clear<Jwt>()
            call.respond(HttpStatusCode.OK)
        }
    }
}
