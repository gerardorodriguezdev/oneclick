package theoneclick.server.app.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import theoneclick.server.shared.di.Environment
import theoneclick.shared.timeProvider.TimeProvider
import kotlin.time.Duration.Companion.seconds

fun Application.configureRateLimit(
    environment: Environment,
    timeProvider: TimeProvider
) {
    install(RateLimit) {
        global {
            val limit = if (environment.disableRateLimit) Int.MAX_VALUE else RateLimitConstants.RATE_LIMIT
            rateLimiter(limit = limit, refillPeriod = 60.seconds, clock = { timeProvider.currentTimeMillis() })
        }
    }
}

private object RateLimitConstants {
    const val RATE_LIMIT = 50
}
