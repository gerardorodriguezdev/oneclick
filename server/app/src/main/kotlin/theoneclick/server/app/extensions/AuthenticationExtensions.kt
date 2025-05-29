package theoneclick.server.app.extensions

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import theoneclick.server.app.plugins.authentication.AuthenticationConstants

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
