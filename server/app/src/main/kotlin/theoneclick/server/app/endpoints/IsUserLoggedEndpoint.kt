package theoneclick.server.app.endpoints

import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.app.extensions.defaultAuthentication
import theoneclick.server.app.models.UserSession
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint
import theoneclick.shared.contracts.core.responses.UserLoggedResponse

fun Routing.isUserLoggedEndpoint() {
    defaultAuthentication(optional = true) {
        get(ClientEndpoint.IS_USER_LOGGED.route) {
            val userSession = call.principal<UserSession>()

            if (userSession == null) {
                call.respond<UserLoggedResponse>(UserLoggedResponse.NotLogged)
            } else {
                call.respond<UserLoggedResponse>(UserLoggedResponse.Logged)
            }
        }
    }
}
