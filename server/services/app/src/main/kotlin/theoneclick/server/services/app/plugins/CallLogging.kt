package theoneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.util.logging.*
import org.slf4j.event.Level
import theoneclick.shared.timeProvider.TimeProvider

internal fun Application.configureCallLogging(
    logger: Logger,
    timeProvider: TimeProvider,
) {
    install(CallLogging) {
        level = Level.DEBUG
        this.logger = logger

        clock { timeProvider.currentTimeMillis() }

        callIdMdc("call-id")
    }
}
