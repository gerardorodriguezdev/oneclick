package theoneclick.server.core.endpoints.devices

import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.extensions.defaultAuthentication
import theoneclick.server.core.plugins.koin.inject
import theoneclick.shared.core.models.endpoints.ClientEndpoint
import theoneclick.shared.core.models.responses.DevicesResponse

fun Routing.devicesEndpoint() {
    val userDataSource: UserDataSource by inject()

    defaultAuthentication {
        get(path = ClientEndpoint.DEVICES.route) {
            val userData = userDataSource.userData()
            val devices = userData?.devices?.toList() ?: emptyList()
            call.respond(
                DevicesResponse(
                    devices = devices,
                )
            )
        }
    }
}
