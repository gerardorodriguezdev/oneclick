package theoneclick.server.core.extensions

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import theoneclick.server.core.plugins.authentication.AuthenticationConstants

fun Routing.defaultAuthentication(block: Route.() -> Unit): Route =
    authenticate(
        configurations = arrayOf(
            AuthenticationConstants.SESSION_AUTHENTICATION,
            AuthenticationConstants.TOKEN_AUTHENTICATION,
        ),
        build = block,
    )