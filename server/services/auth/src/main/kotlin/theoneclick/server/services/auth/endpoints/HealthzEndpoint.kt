package theoneclick.server.services.auth.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.healthzEndpoint() {
    get("/api/healthz/auth") {
        call.respond(HttpStatusCode.OK)
    }
}
