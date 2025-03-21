package theoneclick.server.core.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import theoneclick.shared.core.models.endpoints.Endpoint

fun Routing.healthzEndpoint() {
    get(Endpoint.HEALTHZ.route) {
        call.respond(HttpStatusCode.OK)
    }
}
