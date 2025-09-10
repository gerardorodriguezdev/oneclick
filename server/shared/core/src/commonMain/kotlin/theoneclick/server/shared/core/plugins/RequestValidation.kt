package theoneclick.server.shared.core.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

internal fun Application.configureRequestValidation() {
    install(RequestValidation) {
        // Only used for content length
        validateContentLength()
    }
}
