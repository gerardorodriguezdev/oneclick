package theoneclick.shared.timeProvider

import kotlinx.datetime.Clock

interface TimeProvider {
    fun currentTimeMillis(): Long
}

class SystemTimeProvider : TimeProvider {
    override fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
