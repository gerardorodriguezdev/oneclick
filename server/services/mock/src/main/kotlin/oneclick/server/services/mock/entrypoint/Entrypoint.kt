package oneclick.server.services.mock.entrypoint

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import oneclick.server.services.mock.utils.mockHomes
import oneclick.server.services.mock.utils.mockJwt
import oneclick.server.shared.utils.clientType
import oneclick.shared.contracts.auth.models.requests.LoginRequest.UserRequestLoginRequest
import oneclick.shared.contracts.auth.models.responses.IsLoggedResponse
import oneclick.shared.contracts.auth.models.responses.RequestLoginResponse
import oneclick.shared.contracts.core.models.ClientType
import oneclick.shared.contracts.core.models.NonNegativeInt
import oneclick.shared.contracts.core.models.endpoints.ClientEndpoint
import oneclick.shared.contracts.homes.models.responses.HomesResponse

internal fun server(): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> =
    embeddedServer(
        factory = Netty,
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
        get(ClientEndpoint.IS_LOGGED.route) {
            call.respond<IsLoggedResponse>(IsLoggedResponse.Logged)
        }

        post(ClientEndpoint.USER_REQUEST_LOGIN.route) { _: UserRequestLoginRequest ->
            when (call.request.clientType) {
                ClientType.MOBILE -> call.respond(RequestLoginResponse(jwt = mockJwt()))
                ClientType.BROWSER -> call.respond(HttpStatusCode.OK)
                else -> call.respond(HttpStatusCode.BadRequest)
            }
        }

        post(ClientEndpoint.USER_HOMES.route) {
            call.respond(
                HomesResponse(
                    data = HomesResponse.Data(
                        homes = mockHomes(5),
                        pageIndex = NonNegativeInt.unsafe(5),
                        canRequestMore = true,
                    )
                )
            )
        }

        get(ClientEndpoint.LOGOUT.route) {
            call.respond(HttpStatusCode.OK)
        }
    }
}
