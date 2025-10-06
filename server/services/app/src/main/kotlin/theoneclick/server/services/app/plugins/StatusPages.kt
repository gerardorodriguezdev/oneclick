package oneclick.server.services.app.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.util.logging.*

internal fun Application.configureStatusPages(logger: Logger) {
    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            logger.error(cause)
            call.respond(HttpStatusCode.BadRequest)
        }

        exception<Throwable> { call, cause ->
            logger.error(cause)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}
