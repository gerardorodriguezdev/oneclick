package theoneclick.server.services.app.endpoints

import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.services.app.plugins.authentication.defaultAuthentication
import theoneclick.server.shared.auth.models.JwtPayload
import theoneclick.shared.contracts.auth.models.responses.UserLoggedResponse
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint

internal fun Routing.isUserLoggedEndpoint() {
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
