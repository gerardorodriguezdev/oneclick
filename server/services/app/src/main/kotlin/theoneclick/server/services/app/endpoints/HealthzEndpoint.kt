package theoneclick.server.services.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.healthzEndpoint() {
    get("/api/healthz") { //TODO: Update
        call.respond(HttpStatusCode.OK)
    }
}