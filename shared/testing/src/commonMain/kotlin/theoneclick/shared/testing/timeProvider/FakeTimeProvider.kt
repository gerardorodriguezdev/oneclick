@file:Suppress("NoVarsInConstructor")

package theoneclick.shared.testing.timeProvider

import theoneclick.shared.timeProvider.TimeProvider

class FakeTimeProvider(var fakeCurrentTimeInMillis: Long) : TimeProvider {
    override fun currentTimeMillis(): Long = fakeCurrentTimeInMillis
}
