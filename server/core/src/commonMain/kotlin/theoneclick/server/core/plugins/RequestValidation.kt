package theoneclick.server.core.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureRequestValidation() {
    install(RequestValidation) {
        // Only used for content length
        validateContentLength()
    }
}
