package theoneclick.client.core.platform

import io.ktor.client.engine.*
import theoneclick.client.core.buildkonfig.BuildKonfig
import theoneclick.client.core.extensions.urlProtocol
import theoneclick.shared.core.idlingResources.EmptyIdlingResource
import theoneclick.shared.core.idlingResources.IdlingResource
import theoneclick.shared.core.routes.AppRoute
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.dispatchers.platform.dispatchersProvider
import theoneclick.shared.timeProvider.SystemTimeProvider
import theoneclick.shared.timeProvider.TimeProvider

class AndroidAppDependencies : AppDependencies {
    override val environment: Environment =
        Environment(
            protocol = BuildKonfig.urlProtocol(),
            host = BuildKonfig.HOST,
            port = BuildKonfig.PORT,
            isDebug = BuildKonfig.IS_DEBUG,
        )
    override val dispatchersProvider: DispatchersProvider = dispatchersProvider()
    override val startingRoute: AppRoute = AppRoute.Home
    override val timeProvider: TimeProvider = SystemTimeProvider()
    override val httpEngine: HttpClientEngine = androidHttpClientEngine(timeProvider = timeProvider)
    override val idlingResource: IdlingResource = EmptyIdlingResource()
}

actual fun appDependencies(): AppDependencies = AndroidAppDependencies()
