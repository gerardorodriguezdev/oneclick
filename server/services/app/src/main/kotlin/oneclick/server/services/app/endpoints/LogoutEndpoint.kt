package oneclick.server.services.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import oneclick.server.services.app.dataSources.base.InvalidJwtDataSource
import oneclick.server.services.app.plugins.authentication.defaultAuthentication
import oneclick.server.services.app.plugins.authentication.requireJwtCredentials
import oneclick.shared.contracts.auth.models.Jwt
import oneclick.shared.contracts.core.models.endpoints.ClientEndpoint

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
