package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.extensions.defaultAuthentication
import theoneclick.shared.contracts.core.dtos.TokenDto
import theoneclick.shared.contracts.core.dtos.responses.HomesResponseDto
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint

fun Routing.homesListEndpoint() {
    val usersDataSource: UsersDataSource by inject()

    defaultAuthentication {
        get(ClientEndpoint.HOMES.route) {
            val token = call.principal<TokenDto>()!!
            val user = usersDataSource.user(token)
            if (user == null) {
                call.respond(HttpStatusCode.InternalServerError)
            } else {
                call.respond(HomesResponseDto(homes = user.homes))
            }
        }
    }
}
