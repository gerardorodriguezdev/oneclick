package oneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import oneclick.server.shared.auth.security.UuidProvider

internal fun Application.configureCallId(uuidProvider: UuidProvider) {
    install(CallId) {
        generate {
            uuidProvider.uuid().value
        }
    }
}