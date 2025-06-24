package theoneclick.server.shared.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import io.ktor.util.logging.*
import org.slf4j.event.Level
import theoneclick.server.shared.models.endpoints.ServerEndpoint
import theoneclick.shared.timeProvider.TimeProvider

fun Application.configureCallLogging(
    logger: Logger,
    timeProvider: TimeProvider,
) {
    install(CallLogging) {
        level = Level.DEBUG
        this.logger = logger

        clock { timeProvider.currentTimeMillis() }

        filter { call ->
            call.request.path() != ServerEndpoint.HEALTHZ.route
        }

        callIdMdc("call-id")
    }
}
