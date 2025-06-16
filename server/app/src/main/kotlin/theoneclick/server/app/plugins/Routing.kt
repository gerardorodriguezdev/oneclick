package theoneclick.server.app.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import theoneclick.server.app.dataSources.UsersDataSource
import theoneclick.server.app.di.Environment
import theoneclick.server.app.endpoints.*
import theoneclick.server.app.security.Encryptor
import theoneclick.server.app.security.UuidProvider

fun Application.configureRouting(
    environment: Environment,
    usersDataSource: UsersDataSource,
    encryptor: Encryptor,
    uuidProvider: UuidProvider,
) {
    routing {
        healthzEndpoint()
        requestLoginEndpoint(usersDataSource, encryptor, uuidProvider)
        isUserLoggedEndpoint()
        logoutEndpoint(usersDataSource)
        homesListEndpoint(usersDataSource)

        if (environment.enableQAAPI) {
            qaapi(usersDataSource)
        }
    }
}
