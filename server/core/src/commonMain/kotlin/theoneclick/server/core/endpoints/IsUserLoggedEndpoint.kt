package theoneclick.server.core.endpoints

import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.core.extensions.agent
import theoneclick.server.core.extensions.defaultAuthentication
import theoneclick.server.core.models.UserSession
import theoneclick.shared.core.models.agents.Agent
import theoneclick.shared.core.models.endpoints.ClientEndpoint
import theoneclick.shared.core.models.responses.UserLoggedResponse

fun Routing.isUserLoggedEndpoint() {
    defaultAuthentication(optional = true) {
        get(ClientEndpoint.IS_USER_LOGGED.route) {
            val userSession = userSession()

            if (userSession == null) {
                call.respond<UserLoggedResponse>(UserLoggedResponse.NotLogged)
            } else {
                call.respond<UserLoggedResponse>(UserLoggedResponse.Logged)
            }
        }
    }
}

private fun RoutingContext.userSession(): UserSession? =
    when (call.request.agent) {
        Agent.MOBILE -> call.principal<UserSession>()
        Agent.BROWSER -> call.sessions.get<UserSession>()
    }