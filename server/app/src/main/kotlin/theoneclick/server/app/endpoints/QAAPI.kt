package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.models.UserDto
import theoneclick.server.app.models.endpoints.ServerEndpoint
import theoneclick.shared.contracts.core.dtos.TokenDto

fun Routing.qaapi(usersDataSource: UsersDataSource) {
    post(ServerEndpoint.ADD_USER_DATA.route) { user: UserDto ->
        usersDataSource.saveUser(user)
        call.respond(HttpStatusCode.OK)
    }

    post(ServerEndpoint.ADD_USER_SESSION.route) { token: TokenDto ->
        call.sessions.set(token)
        call.respond(HttpStatusCode.OK)
    }
}
