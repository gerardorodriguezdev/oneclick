package oneclick.server.services.app.plugins.authentication

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import oneclick.server.services.app.plugins.authentication.JwtCredentials.HomeJwtCredentials
import oneclick.server.services.app.plugins.authentication.JwtCredentials.UserJwtCredentials

internal fun Routing.allAuthentication(
    optional: Boolean = false,
    block: Route.() -> Unit
): Route =
    authenticate(
        configurations = AuthenticationType
            .entries
            .map { authenticationType -> authenticationType.value }
            .toTypedArray(),
        optional = optional,
        build = block,
    )

internal fun RoutingContext.requireJwtCredentials(): JwtCredentials =
    requireNotNull(call.principal<UserJwtCredentials>() ?: call.principal<HomeJwtCredentials>())

internal fun Routing.userAuthentication(
    optional: Boolean = false,
    block: Route.() -> Unit
): Route =
    authenticate(
        configurations = arrayOf(
            AuthenticationType.USER_SESSION.value,
            AuthenticationType.USER_JWT.value
        ),
        optional = optional,
        build = block,
    )

internal fun RoutingContext.requireUserJwtCredentials(): UserJwtCredentials =
    requireNotNull(call.principal<UserJwtCredentials>())

internal fun Routing.homeAuthentication(
    optional: Boolean = false,
    block: Route.() -> Unit
): Route =
    authenticate(
        configurations = arrayOf(
            AuthenticationType.HOME_JWT.value
        ),
        optional = optional,
        build = block,
    )

internal fun RoutingContext.requireHomeJwtCredentials(): HomeJwtCredentials =
    requireNotNull(call.principal<HomeJwtCredentials>())