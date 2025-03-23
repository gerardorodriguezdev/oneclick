package theoneclick.client.core.testing.idlingResources

import kotlinx.atomicfu.atomic
import theoneclick.client.core.idlingResources.IdlingResource
import androidx.compose.ui.test.IdlingResource as ComposeIdlingResource

class TestIdlingResource : IdlingResource, ComposeIdlingResource {
    private val counter = atomic(0)

    override fun increment() {
        counter.incrementAndGet()
    }

    override fun decrement() {
        counter.decrementAndGet()
    }

    override val isIdleNow: Boolean
        get() = counter.value == 0
}
