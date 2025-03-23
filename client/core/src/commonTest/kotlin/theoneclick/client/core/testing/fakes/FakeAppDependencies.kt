package theoneclick.client.core.testing.fakes

import io.ktor.client.HttpClient
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import theoneclick.client.core.platform.AppDependencies
import theoneclick.client.core.platform.AuthenticationDataSource
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.dispatchers.platform.dispatchersProvider

class FakeAppDependencies(
    var mockEngine: MockEngine,
) : AppDependencies {
    override val environment: Environment = Environment(urlProtocol = null, host = null, port = null, isDebug = true)
    override val dispatchersProvider: DispatchersProvider = dispatchersProvider()
    override val httpClient: HttpClient =
    override val authenticationDataSource: AuthenticationDataSource =
}
