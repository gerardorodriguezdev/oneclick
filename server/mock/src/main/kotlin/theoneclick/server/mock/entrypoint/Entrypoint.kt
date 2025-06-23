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
import theoneclick.shared.contracts.core.models.agents.Agent
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.PaginationResult
import theoneclick.shared.contracts.core.models.PositiveLong
import theoneclick.shared.contracts.core.models.Token
import theoneclick.shared.contracts.core.models.requests.RequestLoginRequestDto
import theoneclick.shared.contracts.core.models.responses.HomesResponseDto
import theoneclick.shared.contracts.core.models.responses.RequestLoginResponseDto
import theoneclick.shared.contracts.core.models.responses.UserLoggedResponseDto
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint

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
                Agent.MOBILE -> call.respond(RequestLoginResponseDto(token = Token.unsafe("token")))
                Agent.BROWSER -> call.respond(HttpStatusCode.OK)
            }
        }

        get(ClientEndpoint.HOMES.route) {
            call.respond(
                HomesResponseDto(
                    paginationResultDto = PaginationResult(
                        lastModified = PositiveLong.unsafe(1),
                        value = mockHomes(5),
                        pageIndex = NonNegativeInt.unsafe(5),
                        totalPages = NonNegativeInt.unsafe(10),
                    )
                )
            )
        }

        get(ClientEndpoint.LOGOUT.route) {
            call.respond(HttpStatusCode.OK)
        }
    }
}