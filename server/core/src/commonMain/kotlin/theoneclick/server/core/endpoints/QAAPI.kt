package theoneclick.server.core.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import theoneclick.server.core.models.UserData
import theoneclick.server.core.models.UserSession
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.endpoints.authorize.AuthorizeParams
import theoneclick.server.core.plugins.koin.inject
import theoneclick.server.core.models.endpoints.ServerEndpoints

fun Routing.qaapi() {
    val userDataSource: UserDataSource by inject()

    post(ServerEndpoints.ADD_USER_DATA.route) { userData: UserData ->
        userDataSource.saveUserData(userData)
        call.respond(HttpStatusCode.OK)
    }

    post(ServerEndpoints.ADD_USER_SESSION.route) { userSession: UserSession ->
        call.sessions.set(userSession)
        call.respond(HttpStatusCode.OK)
    }

    post(ServerEndpoints.ADD_AUTHORIZE_REDIRECT.route) { authorizeParams: AuthorizeParams ->
        call.sessions.set(authorizeParams)
        call.respond(HttpStatusCode.OK)
    }
}
