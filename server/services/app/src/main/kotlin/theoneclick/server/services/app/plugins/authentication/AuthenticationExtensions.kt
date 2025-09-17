package theoneclick.server.services.app.plugins.authentication

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import theoneclick.server.shared.auth.models.JwtPayload

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

internal fun RoutingContext.requireJwtPayload(): JwtPayload =
    requireNotNull(call.principal<JwtPayload>())