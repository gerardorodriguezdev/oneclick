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
import theoneclick.shared.contracts.core.agents.Agent
import theoneclick.shared.contracts.core.dtos.HomesDto
import theoneclick.shared.contracts.core.dtos.PositiveLongDto
import theoneclick.shared.contracts.core.dtos.TokenDto
import theoneclick.shared.contracts.core.dtos.requests.RequestLoginRequestDto
import theoneclick.shared.contracts.core.dtos.responses.HomesResponseDto
import theoneclick.shared.contracts.core.dtos.responses.RequestLoginResponseDto
import theoneclick.shared.contracts.core.dtos.responses.UserLoggedResponseDto
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint

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
            call.respond<UserLoggedResponseDto>(UserLoggedResponseDto.LoggedDto)
        }

        post(ClientEndpoint.REQUEST_LOGIN.route) { requestLoginRequestDto: RequestLoginRequestDto ->
            when (call.request.agent) {
                Agent.MOBILE -> call.respond(RequestLoginResponseDto(token = TokenDto.unsafe("token")))
                Agent.BROWSER -> call.respond(HttpStatusCode.OK)
            }
        }

        get(ClientEndpoint.HOMES.route) {
            call.respond(
                HomesResponseDto(
                    homes = HomesDto(
                        lastModified = PositiveLongDto.unsafe(1),
                        homes = mockHomes(5)
                    ),
                )
            )
        }

        get(ClientEndpoint.LOGOUT.route) {
            call.respond(HttpStatusCode.OK)
        }
    }
}