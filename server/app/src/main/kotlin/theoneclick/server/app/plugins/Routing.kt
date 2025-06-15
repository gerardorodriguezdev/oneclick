package theoneclick.server.app.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import theoneclick.server.app.endpoints.*
import theoneclick.server.app.platform.Environment

fun Application.configureRouting() {
    val environment: Environment by inject()

    routing {
        healthzEndpoint()
        requestLoginEndpoint()
        isUserLoggedEndpoint()
        logoutEndpoint()
        //TODO: Homes

        if (environment.enableQAAPI) {
            qaapi()
        }
    }
}
