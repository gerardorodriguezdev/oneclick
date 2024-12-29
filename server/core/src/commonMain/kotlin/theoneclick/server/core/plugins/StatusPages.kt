package theoneclick.server.core.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.util.logging.*
import theoneclick.server.core.plugins.koin.inject

fun Application.configureStatusPages() {
    val logger: Logger by inject()

    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            logger.debug(cause.stackTraceToString())
            call.respond(HttpStatusCode.BadRequest)
        }

        exception<Throwable> { call, cause ->
            logger.debug(cause.stackTraceToString())
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}
