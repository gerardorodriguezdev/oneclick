package theoneclick.server.core.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.core.dataSources.UsersDataSource
import theoneclick.server.core.models.User
import theoneclick.server.core.models.UserSession
import theoneclick.server.core.models.endpoints.ServerEndpoint
import theoneclick.server.core.plugins.koin.inject

fun Routing.qaapi() {
    val usersDataSource: UsersDataSource by inject()

    post(ServerEndpoint.ADD_USER_DATA.route) { user: User ->
        usersDataSource.saveUser(user)
        call.respond(HttpStatusCode.OK)
    }

    post(ServerEndpoint.ADD_USER_SESSION.route) { userSession: UserSession ->
        call.sessions.set(userSession)
        call.respond(HttpStatusCode.OK)
    }
}
