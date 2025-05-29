package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.app.models.endpoints.ServerEndpoint

fun Routing.healthzEndpoint() {
    get(ServerEndpoint.HEALTHZ.route) {
        call.respond(HttpStatusCode.OK)
    }
}
