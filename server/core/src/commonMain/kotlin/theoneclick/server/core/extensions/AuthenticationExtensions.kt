package theoneclick.server.core.extensions

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import theoneclick.server.core.plugins.authentication.AuthenticationConstants

fun Routing.userSessionAuthentication(block: Route.() -> Unit): Route =
    authenticate(
        configurations = arrayOf(AuthenticationConstants.AUTH_SESSION),
        build = block,
    )