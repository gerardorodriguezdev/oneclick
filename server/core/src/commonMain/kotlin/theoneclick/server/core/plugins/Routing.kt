package theoneclick.server.core.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import theoneclick.server.core.endpoints.addDevice.addDeviceEndpoint
import theoneclick.server.core.endpoints.devices.devicesEndpoint
import theoneclick.server.core.endpoints.healthzEndpoint
import theoneclick.server.core.endpoints.isUserLoggedEndpoint
import theoneclick.server.core.endpoints.qaapi
import theoneclick.server.core.endpoints.requestLogin.requestLoginEndpoint
import theoneclick.server.core.endpoints.updateDeviceEndpoint.updateDeviceEndpoint
import theoneclick.server.core.platform.Environment
import theoneclick.server.core.plugins.koin.inject

fun Application.configureRouting() {
    val environment: Environment by inject()

    routing {
        healthzEndpoint()
        requestLoginEndpoint()
        isUserLoggedEndpoint()
        addDeviceEndpoint()
        devicesEndpoint()
        updateDeviceEndpoint()

        if (environment.enableQAAPI) {
            qaapi()
        }
    }
}
