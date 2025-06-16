package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.extensions.defaultAuthentication
import theoneclick.server.app.extensions.requireToken
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint

fun Routing.logoutEndpoint(usersDataSource: UsersDataSource) {
    defaultAuthentication {
        get(ClientEndpoint.LOGOUT.route) {
            val token = requireToken()
            val currentUser = usersDataSource.user(token)
            val updatedUser = currentUser?.copy(sessionToken = null)
            updatedUser?.let {
                usersDataSource.saveUser(updatedUser)
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}
