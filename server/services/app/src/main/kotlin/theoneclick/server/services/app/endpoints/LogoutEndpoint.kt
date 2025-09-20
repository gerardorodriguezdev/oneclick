package theoneclick.server.services.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.services.app.dataSources.base.InvalidJwtDataSource
import theoneclick.server.services.app.plugins.authentication.defaultAuthentication
import theoneclick.server.services.app.plugins.authentication.requireJwtCredentials
import theoneclick.shared.contracts.auth.models.Jwt
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint

internal fun Routing.logoutEndpoint(invalidJwtDataSource: InvalidJwtDataSource) {
    defaultAuthentication {
        get(ClientEndpoint.LOGOUT.route) {
            val jwtCredentials = requireJwtCredentials()
            invalidJwtDataSource.saveInvalidJwt(jwtCredentials.jti)
            call.sessions.clear<Jwt>()
            call.respond(HttpStatusCode.OK)
        }
    }
}
