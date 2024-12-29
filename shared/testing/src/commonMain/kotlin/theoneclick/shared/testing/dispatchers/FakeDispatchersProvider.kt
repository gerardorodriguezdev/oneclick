package theoneclick.shared.testing.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import theoneclick.shared.dispatchers.platform.DispatchersProvider

class FakeDispatchersProvider(
    var fakeMain: CoroutineDispatcher,
    var fakeIo: CoroutineDispatcher,
) : DispatchersProvider {

    constructor(coroutineDispatcher: CoroutineDispatcher) : this(coroutineDispatcher, coroutineDispatcher)

    override fun main(): CoroutineDispatcher = fakeMain
    override fun io(): CoroutineDispatcher = fakeIo
}
