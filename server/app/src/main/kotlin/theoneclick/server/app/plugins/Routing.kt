package theoneclick.server.app.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import theoneclick.server.app.endpoints.addDevice.addDeviceEndpoint
import theoneclick.server.app.endpoints.devices.devicesEndpoint
import theoneclick.server.app.endpoints.healthzEndpoint
import theoneclick.server.app.endpoints.isUserLoggedEndpoint
import theoneclick.server.app.endpoints.logoutEndpoint
import theoneclick.server.app.endpoints.qaapi
import theoneclick.server.app.endpoints.requestLogin.requestLoginEndpoint
import theoneclick.server.app.endpoints.updateDeviceEndpoint.updateDeviceEndpoint
import theoneclick.server.app.platform.Environment
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val environment: Environment by inject()

    routing {
        healthzEndpoint()
        requestLoginEndpoint()
        isUserLoggedEndpoint()
        addDeviceEndpoint()
        devicesEndpoint()
        updateDeviceEndpoint()
        logoutEndpoint()

        if (environment.enableQAAPI) {
            qaapi()
        }
    }
}
