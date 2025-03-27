package theoneclick.server.core.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.core.dataSources.UserDataSource
import theoneclick.server.core.extensions.defaultAuthentication
import theoneclick.server.core.plugins.koin.inject
import theoneclick.shared.core.models.endpoints.ClientEndpoint

//TODO: Test
fun Routing.logoutEndpoint() {
    val userDataSource: UserDataSource by inject()

    defaultAuthentication {
        get(ClientEndpoint.LOGOUT.route) {
            val currentUserData = userDataSource.userData()
            val newUserData = currentUserData?.copy(sessionToken = null)
            newUserData?.let {
                userDataSource.saveUserData(newUserData)
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}
