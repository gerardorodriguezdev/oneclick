package theoneclick.shared.timeProvider

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

interface TimeProvider {
    fun currentTimeMillis(): Long
}

class SystemTimeProvider : TimeProvider {
    @OptIn(ExperimentalTime::class)
    override fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
