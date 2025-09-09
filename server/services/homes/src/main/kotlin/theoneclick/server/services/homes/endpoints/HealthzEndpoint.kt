package theoneclick.server.services.homes.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.healthzEndpoint() {
    get("/api/healthz/homes") {
        call.respond(HttpStatusCode.OK)
    }
}
