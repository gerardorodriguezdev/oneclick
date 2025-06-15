package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.models.User
import theoneclick.server.app.models.Token
import theoneclick.server.app.models.endpoints.ServerEndpoint
import org.koin.ktor.ext.inject

fun Routing.qaapi() {
    val usersDataSource: UsersDataSource by inject()

    post(ServerEndpoint.ADD_USER_DATA.route) { user: User ->
        usersDataSource.saveUser(user)
        call.respond(HttpStatusCode.OK)
    }

    post(ServerEndpoint.ADD_USER_SESSION.route) { token: Token ->
        call.sessions.set(token)
        call.respond(HttpStatusCode.OK)
    }
}
