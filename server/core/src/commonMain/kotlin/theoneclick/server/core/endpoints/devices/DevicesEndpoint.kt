package theoneclick.server.core.endpoints.devices

import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.core.dataSources.UsersDataSource
import theoneclick.server.core.extensions.defaultAuthentication
import theoneclick.server.core.models.UserSession
import theoneclick.server.core.plugins.koin.inject
import theoneclick.shared.core.models.endpoints.ClientEndpoint
import theoneclick.shared.core.models.responses.DevicesResponse

fun Routing.devicesEndpoint() {
    val usersDataSource: UsersDataSource by inject()

    defaultAuthentication {
        get(path = ClientEndpoint.DEVICES.route) {
            val userSession = call.principal<UserSession>()!!
            val sessionToken = userSession.sessionToken
            val user = usersDataSource.user(sessionToken)
            val devices = user?.devices?.toList() ?: emptyList()

            call.respond(
                DevicesResponse(
                    devices = devices,
                )
            )
        }
    }
}
