package theoneclick.server.core.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import theoneclick.server.core.platform.Environment
import theoneclick.server.core.plugins.koin.inject
import theoneclick.shared.timeProvider.TimeProvider
import kotlin.time.Duration.Companion.seconds

fun Application.configureRateLimit() {
    val environment: Environment by inject()
    val timeProvider: TimeProvider by inject()
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
