package theoneclick.server.core.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.extensions.defaultAuthentication
import theoneclick.server.core.plugins.koin.inject
import theoneclick.shared.core.models.endpoints.ClientEndpoint

fun Routing.logoutEndpoint() {
    val userDataSource: UserDataSource by inject()

    defaultAuthentication {
        get(ClientEndpoint.LOGOUT.route) {
            val currentUser = userDataSource.user()
            val newUser = currentUser?.copy(sessionToken = null)
            newUser?.let {
                userDataSource.saveUser(newUser)
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}
