package theoneclick.server.mock.entrypoint

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.shared.core.dataSources.models.endpoints.Endpoint
import theoneclick.shared.core.dataSources.models.entities.Device
import theoneclick.shared.core.dataSources.models.entities.Uuid
import theoneclick.shared.core.dataSources.models.requests.RequestLoginRequest
import theoneclick.shared.core.dataSources.models.responses.DevicesResponse
import theoneclick.shared.core.dataSources.models.responses.RequestLoginResponse
import theoneclick.shared.core.dataSources.models.responses.UserLoggedResponse
import theoneclick.shared.core.routes.AppRoute

fun server(): EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration> =
    embeddedServer(
        factory = CIO,
        port = 3000,
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
        get(Endpoint.IS_USER_LOGGED.route) {
            call.respond<UserLoggedResponse>(UserLoggedResponse.NotLogged)
        }

        post(Endpoint.REQUEST_LOGIN.route) { requestLoginRequest: RequestLoginRequest ->
            call.respond<RequestLoginResponse>(RequestLoginResponse.LocalRedirect(AppRoute.Home))
        }

        get(Endpoint.DEVICES.route) {
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

        post(Endpoint.ADD_DEVICE.route) {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}
