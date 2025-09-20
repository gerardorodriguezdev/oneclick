package theoneclick.server.services.app.plugins.authentication

import io.ktor.server.auth.*
import io.ktor.server.routing.*

internal fun Routing.defaultAuthentication(
    optional: Boolean = false,
    block: Route.() -> Unit
): Route =
    authenticate(
        configurations = arrayOf(
            AuthenticationConstants.JWT_SESSION_AUTHENTICATION,
            AuthenticationConstants.JWT_AUTHENTICATION,
        ),
        optional = optional,
        build = block,
    )

internal fun RoutingContext.requireJwtCredentials(): JwtCredentials =
    requireNotNull(call.principal<JwtCredentials>())
