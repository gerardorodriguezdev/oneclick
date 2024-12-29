package theoneclick.server.core.endpoints

import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.core.data.models.UserSession
import theoneclick.shared.core.dataSources.models.endpoints.Endpoint
import theoneclick.shared.core.dataSources.models.responses.UserLoggedResponse

fun Routing.isUserLoggedEndpoint() {
    get(Endpoint.IS_USER_LOGGED.route) {
        val userSession = call.sessions.get<UserSession>()

        if (userSession == null) {
            call.respond<UserLoggedResponse>(UserLoggedResponse.NotLogged)
        } else {
            call.respond<UserLoggedResponse>(UserLoggedResponse.Logged)
        }
    }
}
