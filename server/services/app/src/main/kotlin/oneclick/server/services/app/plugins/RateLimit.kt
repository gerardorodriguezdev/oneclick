package oneclick.server.services.app.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import oneclick.shared.timeProvider.TimeProvider
import kotlin.time.Duration.Companion.seconds

internal fun Application.configureRateLimit(
    disableRateLimit: Boolean,
    timeProvider: TimeProvider
) {
    install(RateLimit) {
        global {
            val limit = if (disableRateLimit) Int.MAX_VALUE else RateLimitConstants.RATE_LIMIT
            rateLimiter(limit = limit, refillPeriod = 60.seconds, clock = { timeProvider.currentTimeMillis() })
        }
    }
}

private object RateLimitConstants {
    const val RATE_LIMIT = 50
}
