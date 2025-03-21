package theoneclick.server.core.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import io.ktor.util.logging.*
import org.slf4j.event.Level
import theoneclick.server.core.plugins.koin.inject
import theoneclick.server.core.models.endpoints.ServerEndpoints
import theoneclick.shared.timeProvider.TimeProvider

fun Application.configureCallLogging() {
    val logger: Logger by inject()
    val timeProvider: TimeProvider by inject()

    install(CallLogging) {
        level = Level.DEBUG
        this.logger = logger

        clock { timeProvider.currentTimeMillis() }

        filter { call ->
            call.request.path() != ServerEndpoints.HEALTHZ.route
        }

        callIdMdc("call-id")
    }
}
