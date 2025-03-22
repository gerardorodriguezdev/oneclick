package theoneclick.client.core.testing.fakes

import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import theoneclick.client.core.platform.Environment
import theoneclick.client.core.platform.HomeDependencies
import theoneclick.client.core.testing.idlingResources.TestIdlingResource
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.dispatchers.platform.dispatchersProvider

class FakeHomeDependencies(
    var mockEngine: MockEngine,
) : HomeDependencies {
    override val environment: Environment = Environment(urlProtocol = null, host = null, port = null, isDebug = true)
    override val dispatchersProvider: DispatchersProvider = dispatchersProvider()
    override val httpEngine: HttpClientEngine = mockEngine
    override val idlingResource: TestIdlingResource = TestIdlingResource()
}
