package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.extensions.defaultAuthentication
import theoneclick.server.app.models.UserSession
import org.koin.ktor.ext.inject
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
