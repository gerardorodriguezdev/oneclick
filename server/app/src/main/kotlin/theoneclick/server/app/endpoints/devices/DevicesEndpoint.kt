package theoneclick.server.app.endpoints.devices

import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.extensions.defaultAuthentication
import theoneclick.server.app.models.UserSession
import org.koin.ktor.ext.inject
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint
import theoneclick.shared.contracts.core.responses.HomesResponse

fun Routing.devicesEndpoint() {
    val usersDataSource: UsersDataSource by inject()

    defaultAuthentication {
        get(path = ClientEndpoint.HOMES.route) {
            val userSession = call.principal<UserSession>()!!
            val sessionToken = userSession.sessionToken
            val user = usersDataSource.user(sessionToken)
            val devices = user?.devices?.toList() ?: emptyList()

            call.respond(
                HomesResponse(
                    homes = devices,
                )
            )
        }
    }
}
