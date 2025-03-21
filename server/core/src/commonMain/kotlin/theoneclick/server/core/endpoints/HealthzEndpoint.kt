package theoneclick.server.core.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.server.core.models.endpoints.ServerEndpoints

fun Routing.healthzEndpoint() {
    get(ServerEndpoints.HEALTHZ.route) {
        call.respond(HttpStatusCode.OK)
    }
}
