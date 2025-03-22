package theoneclick.client.core.platform

import io.ktor.client.*
import theoneclick.client.core.buildkonfig.BuildKonfig
import theoneclick.client.core.idlingResources.EmptyIdlingResource
import theoneclick.client.core.idlingResources.IdlingResource
import theoneclick.client.core.mappers.urlProtocol
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.dispatchers.platform.dispatchersProvider
import theoneclick.shared.timeProvider.SystemTimeProvider
import theoneclick.shared.timeProvider.TimeProvider

class WasmAppDependencies : AppDependencies {
    override val environment: Environment =
        Environment(
            isDebug = BuildKonfig.IS_DEBUG,
            urlProtocol = BuildKonfig.urlProtocol(),
            host = BuildKonfig.HOST,
            port = BuildKonfig.PORT,
        )
    override val dispatchersProvider: DispatchersProvider = dispatchersProvider()
    override val idlingResource: IdlingResource = EmptyIdlingResource()
    override val timeProvider: TimeProvider = SystemTimeProvider()
    override val httpClient: HttpClient =
        wasmHttpClient(
            urlProtocol = environment.urlProtocol,
            host = environment.host,
            port = environment.port,
        )
}

actual fun appDependencies(): AppDependencies = WasmAppDependencies()