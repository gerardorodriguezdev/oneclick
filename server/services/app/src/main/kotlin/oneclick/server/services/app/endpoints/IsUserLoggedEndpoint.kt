package oneclick.server.services.app.endpoints

import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import oneclick.server.services.app.plugins.authentication.JwtCredentials
import oneclick.server.services.app.plugins.authentication.defaultAuthentication
import oneclick.shared.contracts.auth.models.responses.UserLoggedResponse
import oneclick.shared.contracts.core.models.endpoints.ClientEndpoint

internal fun Routing.isUserLoggedEndpoint() {
    defaultAuthentication(optional = true) {
        get(ClientEndpoint.IS_USER_LOGGED.route) {
            val jwtCredentials = call.principal<JwtCredentials>()

            if (jwtCredentials == null) {
                call.respond<UserLoggedResponse>(UserLoggedResponse.NotLogged)
            } else {
                call.respond<UserLoggedResponse>(UserLoggedResponse.Logged)
            }
        }
    }
}
