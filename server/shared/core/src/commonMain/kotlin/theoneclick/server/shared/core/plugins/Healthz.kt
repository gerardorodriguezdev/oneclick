package theoneclick.server.shared.core.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureHealthz(path: String) {
    routing {
        get(path) {
            call.respond(HttpStatusCode.OK)
        }
    }
}