package theoneclick.server.shared.extensions

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import theoneclick.server.shared.plugins.authentication.AuthenticationConstants
import theoneclick.shared.contracts.core.models.Token

fun Routing.defaultAuthentication(
    optional: Boolean = false,
    block: Route.() -> Unit
): Route =
    authenticate(
        configurations = arrayOf(
            AuthenticationConstants.SESSION_AUTHENTICATION,
            AuthenticationConstants.TOKEN_AUTHENTICATION,
        ),
        optional = optional,
        build = block,
    )

fun RoutingContext.requireToken(): Token = requireNotNull(call.principal<Token>())