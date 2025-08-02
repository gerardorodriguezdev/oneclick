package theoneclick.server.shared.extensions

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import theoneclick.server.shared.models.JwtPayload
import theoneclick.server.shared.plugins.authentication.AuthenticationConstants

fun Routing.defaultAuthentication(
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

fun RoutingContext.requireJwtPayload(): JwtPayload =
    requireNotNull(call.principal<JwtPayload>())