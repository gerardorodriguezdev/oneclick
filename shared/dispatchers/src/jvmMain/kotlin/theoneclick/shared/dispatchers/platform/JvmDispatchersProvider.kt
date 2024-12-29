package theoneclick.shared.dispatchers.platform

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class JvmDispatchersProvider : DispatchersProvider {
    override fun main(): CoroutineDispatcher = Dispatchers.Main
    override fun io(): CoroutineDispatcher = Dispatchers.IO
}

actual fun dispatchersProvider(): DispatchersProvider = JvmDispatchersProvider()
