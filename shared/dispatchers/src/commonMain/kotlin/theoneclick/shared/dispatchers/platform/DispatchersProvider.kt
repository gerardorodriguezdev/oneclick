package theoneclick.shared.dispatchers.platform

import kotlinx.coroutines.CoroutineDispatcher

interface DispatchersProvider {
    fun main(): CoroutineDispatcher
    fun io(): CoroutineDispatcher
}

expect fun dispatchersProvider(): DispatchersProvider
