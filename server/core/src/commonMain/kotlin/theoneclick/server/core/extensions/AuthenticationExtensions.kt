package theoneclick.server.core.extensions

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import theoneclick.server.core.plugins.authentication.AuthenticationConstants

fun Routing.userSessionAuthentication(block: Route.() -> Unit): Route =
    authenticate(
        configurations = arrayOf(AuthenticationConstants.SESSION_AUTHENTICATION),
        build = block,
    )

fun Routing.bearerAuthentication(block: Route.() -> Unit): Route =
    authenticate(
        configurations = arrayOf(AuthenticationConstants.BEARER_AUTHENTICATION),
        build = block,
    )
