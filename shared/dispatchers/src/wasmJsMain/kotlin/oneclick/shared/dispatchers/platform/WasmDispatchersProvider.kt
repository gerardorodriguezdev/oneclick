package oneclick.shared.dispatchers.platform

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class WasmDispatchersProvider : DispatchersProvider {
    override fun main(): CoroutineDispatcher = Dispatchers.Main
    override fun io(): CoroutineDispatcher = Dispatchers.Default
}

actual fun dispatchersProvider(): DispatchersProvider = WasmDispatchersProvider()
