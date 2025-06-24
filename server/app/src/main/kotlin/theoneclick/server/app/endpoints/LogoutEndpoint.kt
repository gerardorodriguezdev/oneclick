package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.shared.dataSources.base.UsersDataSource
import theoneclick.server.shared.extensions.defaultAuthentication
import theoneclick.server.shared.extensions.requireToken
import theoneclick.server.shared.repositories.UsersRepository
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint

fun Routing.logoutEndpoint(usersRepository: UsersRepository) {
    defaultAuthentication {
        get(ClientEndpoint.LOGOUT.route) {
            val token = requireToken()
            val currentUser = usersRepository.user(UsersDataSource.Findable.ByToken(token))
            val updatedUser = currentUser?.copy(sessionToken = null)
            updatedUser?.let {
                usersRepository.saveUser(updatedUser)
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}
