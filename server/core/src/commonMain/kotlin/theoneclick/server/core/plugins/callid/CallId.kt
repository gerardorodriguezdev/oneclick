package theoneclick.server.core.plugins.callid

import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import kotlinx.atomicfu.atomic
import theoneclick.server.core.plugins.koin.inject
import theoneclick.shared.timeProvider.TimeProvider

fun Application.configureCallId() {
    val timeProvider: TimeProvider by inject()
    val callCounter = CallCounter()

    install(CallId) {
        generate {
            val count = callCounter.counter.incrementAndGet()
            val currentTimeInMillis = timeProvider.currentTimeMillis()
            "$count-$currentTimeInMillis"
        }
    }
}

/**
 * Class required to work with atomicfu on common code
 */
private class CallCounter {
    val counter = atomic(0)
}
