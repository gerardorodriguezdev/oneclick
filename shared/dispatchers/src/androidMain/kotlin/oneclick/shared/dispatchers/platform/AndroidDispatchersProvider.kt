package oneclick.shared.dispatchers.platform

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AndroidDispatchersProvider : DispatchersProvider {
    override fun main(): CoroutineDispatcher = Dispatchers.Main
    override fun io(): CoroutineDispatcher = Dispatchers.IO
}

actual fun dispatchersProvider(): DispatchersProvider = AndroidDispatchersProvider()
