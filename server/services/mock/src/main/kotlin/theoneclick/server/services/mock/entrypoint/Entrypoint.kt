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
import oneclick.server.shared.core.agent
import oneclick.shared.contracts.auth.models.requests.RequestLoginRequest
import oneclick.shared.contracts.auth.models.responses.RequestLoginResponse
import oneclick.shared.contracts.auth.models.responses.UserLoggedResponse
import oneclick.shared.contracts.core.models.NonNegativeInt
import oneclick.shared.contracts.core.models.agents.Agent
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
        get(ClientEndpoint.IS_USER_LOGGED.route) {
            call.respond<UserLoggedResponse>(UserLoggedResponse.Logged)
        }

        post(ClientEndpoint.REQUEST_LOGIN.route) { _: RequestLoginRequest ->
            when (call.request.agent) {
                Agent.MOBILE -> call.respond(RequestLoginResponse(jwt = mockJwt()))
                Agent.BROWSER -> call.respond(HttpStatusCode.OK)
            }
        }

        post(ClientEndpoint.HOMES.route) {
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
