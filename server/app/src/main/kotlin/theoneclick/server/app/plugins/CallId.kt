package theoneclick.server.app.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import kotlinx.atomicfu.atomic
import theoneclick.shared.timeProvider.TimeProvider

fun Application.configureCallId(timeProvider: TimeProvider) {
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
