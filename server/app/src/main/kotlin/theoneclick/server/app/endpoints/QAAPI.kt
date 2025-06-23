package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.app.models.User
import theoneclick.server.app.models.endpoints.ServerEndpoint
import theoneclick.server.app.repositories.UsersRepository
import theoneclick.shared.contracts.core.models.Token

fun Routing.qaapi(usersRepository: UsersRepository) {
    post(ServerEndpoint.ADD_USER_DATA.route) { user: User ->
        usersRepository.saveUser(user)
        call.respond(HttpStatusCode.OK)
    }

    post(ServerEndpoint.ADD_USER_SESSION.route) { token: Token ->
        call.sessions.set(token)
        call.respond(HttpStatusCode.OK)
    }
}
