package theoneclick.server.mock.entrypoint

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.shared.core.models.endpoints.ClientEndpoints
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.core.models.entities.Uuid
import theoneclick.shared.core.models.requests.RequestLoginRequest
import theoneclick.shared.core.models.responses.DevicesResponse
import theoneclick.shared.core.models.responses.RequestLoginResponse
import theoneclick.shared.core.models.responses.UserLoggedResponse
import theoneclick.shared.core.models.routes.AppRoute

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

private fun Application.configureRouting() {
    routing {
        get(ClientEndpoints.IS_USER_LOGGED.route) {
            call.respond<UserLoggedResponse>(UserLoggedResponse.NotLogged)
        }

        post(ClientEndpoints.REQUEST_LOGIN.route) { requestLoginRequest: RequestLoginRequest ->
            call.respond<RequestLoginResponse>(RequestLoginResponse.LocalRedirect(AppRoute.Home))
        }

        get(ClientEndpoints.DEVICES.route) {
            call.respond(
                DevicesResponse(
                    devices = listOf(
                        Device.Blind(
                            id = Uuid("1"),
                            deviceName = "Device1",
                            room = "Room1",
                            isOpened = false,
                            rotation = 0,
                        ),
                        Device.Blind(
                            id = Uuid("2"),
                            deviceName = "Device1",
                            room = "Room1",
                            isOpened = false,
                            rotation = 0,
                        ),
                        Device.Blind(
                            id = Uuid("3"),
                            deviceName = "Device1",
                            room = "Room1",
                            isOpened = false,
                            rotation = 0,
                        ),
                    )
                )
            )
        }

        post(ClientEndpoints.ADD_DEVICE.route) {
            call.respondRedirect("/login")
        }
    }
}
