package theoneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import theoneclick.server.shared.auth.security.UuidProvider

internal fun Application.configureCallId(uuidProvider: UuidProvider) {
    install(CallId) {
        generate {
            uuidProvider.uuid().value
        }
    }
}