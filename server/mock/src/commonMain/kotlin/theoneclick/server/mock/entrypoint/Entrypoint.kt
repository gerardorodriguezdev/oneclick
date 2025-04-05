package theoneclick.server.mock.entrypoint

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.shared.extensions.agent
import theoneclick.shared.core.models.agents.Agent
import theoneclick.shared.core.models.endpoints.ClientEndpoint
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.Uuid
import theoneclick.shared.core.models.requests.RequestLoginRequest
import theoneclick.shared.core.models.responses.AddDeviceResponse
import theoneclick.shared.core.models.responses.DevicesResponse
import theoneclick.shared.core.models.responses.RequestLoginResponse
import theoneclick.shared.core.models.responses.UserLoggedResponse

fun server(): EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration> =
    embeddedServer(
        factory = CIO,
        port = 8080,
        module = {
            configureContentNegotiation()
            configureRouting()
        },
    )

private fun Application.configureContentNegotiation() {
    install(ContentNegotiation) {
        json()
    }
}

private val devices = mutableListOf<Device>(
    Device.Blind(
        id = Uuid("1"),
        deviceName = "Device1",
        room = "Room1",
        isOpened = false,
        rotation = 0,
    )
)

private fun Application.configureRouting() {
    routing {
        get(ClientEndpoint.IS_USER_LOGGED.route) {
            call.respond<UserLoggedResponse>(UserLoggedResponse.Logged)
        }

        post(ClientEndpoint.REQUEST_LOGIN.route) { requestLoginRequest: RequestLoginRequest ->
            when (call.request.agent) {
                Agent.MOBILE -> call.respond(RequestLoginResponse("token"))
                Agent.BROWSER -> call.respond(HttpStatusCode.OK)
            }
        }

        get(ClientEndpoint.DEVICES.route) {
            call.respond(
                DevicesResponse(
                    devices = devices
                )
            )
        }

        post(ClientEndpoint.UPDATE_DEVICE.route) {
            call.respond(HttpStatusCode.OK)
        }

        post(ClientEndpoint.ADD_DEVICE.route) {
            val newDevice = Device.Blind(
                id = Uuid(devices.last().id.value + "a"),
                deviceName = "Device1",
                room = "Room1",
                isOpened = false,
                rotation = 0,
            )
            devices.add(newDevice)
            call.respond(
                AddDeviceResponse(newDevice)
            )
        }

        get(ClientEndpoint.LOGOUT.route) {
            call.respond(HttpStatusCode.OK)
        }
    }
}
