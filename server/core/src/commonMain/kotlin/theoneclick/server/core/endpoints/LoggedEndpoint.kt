package theoneclick.server.core.endpoints

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.core.dataSources.UsersDataSource
import theoneclick.server.core.extensions.defaultAuthentication
import theoneclick.server.core.models.UserSession
import theoneclick.server.core.plugins.koin.inject
import theoneclick.shared.core.models.endpoints.ClientEndpoint

fun Routing.logoutEndpoint() {
    val usersDataSource: UsersDataSource by inject()

    defaultAuthentication {
        get(ClientEndpoint.LOGOUT.route) {
            val userSession = call.principal<UserSession>()!!
            val sessionToken = userSession.sessionToken
            val currentUser = usersDataSource.user(sessionToken)
            val newUser = currentUser?.copy(sessionToken = null)
            newUser?.let {
                usersDataSource.saveUser(newUser)
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}
