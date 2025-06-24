package theoneclick.server.mock.entrypoint

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.mock.utils.mockHomes
import theoneclick.server.shared.extensions.agent
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.PositiveLong
import theoneclick.shared.contracts.core.models.Token
import theoneclick.shared.contracts.core.models.agents.Agent
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint
import theoneclick.shared.contracts.core.models.requests.RequestLoginRequest
import theoneclick.shared.contracts.core.models.responses.HomesResponse
import theoneclick.shared.contracts.core.models.responses.RequestLoginResponse
import theoneclick.shared.contracts.core.models.responses.UserLoggedResponse

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
        get(ClientEndpoint.IS_USER_LOGGED.route) {
            call.respond<UserLoggedResponse>(UserLoggedResponse.Logged)
        }

        post(ClientEndpoint.REQUEST_LOGIN.route) { requestLoginRequest: RequestLoginRequest ->
            when (call.request.agent) {
                Agent.MOBILE -> call.respond(RequestLoginResponse(token = Token.unsafe("token")))
                Agent.BROWSER -> call.respond(HttpStatusCode.OK)
            }
        }

        get(ClientEndpoint.HOMES.route) {
            call.respond(
                HomesResponse(
                    data = HomesResponse.Data(
                        lastModified = PositiveLong.unsafe(1),
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