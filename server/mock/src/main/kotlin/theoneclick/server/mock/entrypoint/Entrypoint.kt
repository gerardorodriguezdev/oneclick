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
import theoneclick.shared.contracts.core.agents.Agent
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint
import theoneclick.shared.contracts.core.requests.RequestLoginRequest
import theoneclick.shared.contracts.core.responses.RequestLoginResponse
import theoneclick.shared.contracts.core.responses.UserLoggedResponse

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
        get(ClientEndpoint.IS_USER_LOGGED.route) {
            call.respond<UserLoggedResponse>(UserLoggedResponse.Logged)
        }

        post(ClientEndpoint.REQUEST_LOGIN.route) { requestLoginRequest: RequestLoginRequest ->
            when (call.request.agent) {
                Agent.MOBILE -> call.respond(RequestLoginResponse("token"))
                Agent.BROWSER -> call.respond(HttpStatusCode.OK)
            }
        }

        get(ClientEndpoint.HOMES.route) {
            call.respond(HttpStatusCode.OK)
        }

        get(ClientEndpoint.LOGOUT.route) {
            call.respond(HttpStatusCode.OK)
        }
    }
}
