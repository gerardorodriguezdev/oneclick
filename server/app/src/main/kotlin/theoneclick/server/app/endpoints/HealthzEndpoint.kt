package theoneclick.server.app.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.healthzEndpoint() {
    get("/api/healthz/app") {
        call.respond(HttpStatusCode.OK)
    }
}
