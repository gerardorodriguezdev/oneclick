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

class AndroidAppDependencies : AppDependencies {
    override val environment: Environment =
        Environment(
            isDebug = BuildKonfig.IS_DEBUG,
            urlProtocol = BuildKonfig.urlProtocol(),
        )
    override val dispatchersProvider: DispatchersProvider = dispatchersProvider()
    override val timeProvider: TimeProvider = SystemTimeProvider()
    override val idlingResource: IdlingResource = EmptyIdlingResource()
    override val httpClient: HttpClient = androidHttpClient(
        urlProtocol = environment.urlProtocol,
        timeProvider = timeProvider,
        tokenProvider = DiskTokenProvider(),
    )
}

actual fun appDependencies(): AppDependencies = AndroidAppDependencies()