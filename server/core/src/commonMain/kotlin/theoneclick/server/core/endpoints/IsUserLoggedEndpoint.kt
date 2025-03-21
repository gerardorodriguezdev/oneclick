package theoneclick.server.core.endpoints

import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.core.models.UserSession
import theoneclick.shared.core.models.endpoints.ClientEndpoints
import theoneclick.shared.core.models.responses.UserLoggedResponse

fun Routing.isUserLoggedEndpoint() {
    get(ClientEndpoints.IS_USER_LOGGED.route) {
        val userSession = call.sessions.get<UserSession>()

        if (userSession == null) {
            call.respond<UserLoggedResponse>(UserLoggedResponse.NotLogged)
        } else {
            call.respond<UserLoggedResponse>(UserLoggedResponse.Logged)
        }
    }
}
