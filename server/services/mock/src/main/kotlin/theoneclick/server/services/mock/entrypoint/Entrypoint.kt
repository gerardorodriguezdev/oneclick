package theoneclick.server.services.mock.entrypoint

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.services.mock.utils.mockHomes
import theoneclick.server.services.mock.utils.mockJwt
import theoneclick.server.shared.core.extensions.agent
import theoneclick.shared.contracts.auth.models.requests.RequestLoginRequest
import theoneclick.shared.contracts.auth.models.responses.RequestLoginResponse
import theoneclick.shared.contracts.auth.models.responses.UserLoggedResponse
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.agents.Agent
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint
import theoneclick.shared.contracts.homes.models.responses.HomesResponse

fun server(): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> =
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
        get(ClientEndpoint.IS_USER_LOGGED) {
            call.respond<UserLoggedResponse>(UserLoggedResponse.Logged)
        }

        post(ClientEndpoint.REQUEST_LOGIN) { _: RequestLoginRequest ->
            when (call.request.agent) {
                Agent.MOBILE -> call.respond(RequestLoginResponse(jwt = mockJwt()))
                Agent.BROWSER -> call.respond(HttpStatusCode.OK)
            }
        }

        post(ClientEndpoint.HOMES) {
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

        get(ClientEndpoint.LOGOUT) {
            call.respond(HttpStatusCode.OK)
        }
    }
}
