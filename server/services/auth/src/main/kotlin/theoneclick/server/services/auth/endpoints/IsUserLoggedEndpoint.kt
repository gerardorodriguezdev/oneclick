package theoneclick.server.services.auth.endpoints

import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.shared.extensions.defaultAuthentication
import theoneclick.server.shared.models.JwtPayload
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint
import theoneclick.shared.contracts.core.models.responses.UserLoggedResponse

fun Routing.isUserLoggedEndpoint() {
    defaultAuthentication(optional = true) {
        get(ClientEndpoint.IS_USER_LOGGED) {
            val jwtPayload = call.principal<JwtPayload>()

            if (jwtPayload == null) {
                call.respond<UserLoggedResponse>(UserLoggedResponse.NotLogged)
            } else {
                call.respond<UserLoggedResponse>(UserLoggedResponse.Logged)
            }
        }
    }
}
