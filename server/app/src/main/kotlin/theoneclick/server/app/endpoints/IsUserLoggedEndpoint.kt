package theoneclick.server.app.endpoints

import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.app.extensions.defaultAuthentication
import theoneclick.shared.contracts.core.dtos.TokenDto
import theoneclick.shared.contracts.core.dtos.responses.UserLoggedResponseDto
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint

fun Routing.isUserLoggedEndpoint() {
    defaultAuthentication(optional = true) {
        get(ClientEndpoint.IS_USER_LOGGED.route) {
            val token = call.principal<TokenDto>()

            if (token == null) {
                call.respond<UserLoggedResponseDto>(UserLoggedResponseDto.NotLoggedDto)
            } else {
                call.respond<UserLoggedResponseDto>(UserLoggedResponseDto.LoggedDto)
            }
        }
    }
}
