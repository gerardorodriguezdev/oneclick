package theoneclick.server.services.auth.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.shared.models.User
import theoneclick.server.shared.models.endpoints.ServerEndpoint
import theoneclick.server.shared.repositories.UsersRepository
import theoneclick.shared.contracts.core.models.Jwt

fun Routing.qaapi(usersRepository: UsersRepository) {
    post(ServerEndpoint.ADD_USER_DATA.route) { user: User ->
        usersRepository.saveUser(user)
        call.respond(HttpStatusCode.OK)
    }

    post(ServerEndpoint.ADD_USER_SESSION.route) { jwt: Jwt ->
        call.sessions.set(jwt)
        call.respond(HttpStatusCode.OK)
    }
}
