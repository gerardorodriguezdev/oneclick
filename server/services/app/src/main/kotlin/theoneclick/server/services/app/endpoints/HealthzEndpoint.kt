package theoneclick.server.services.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.services.app.models.ServerEndpoint

internal fun Routing.healthzEndpoint() {
    get(ServerEndpoint.HEALTHZ.route) {
        call.respond(HttpStatusCode.OK)
    }
}