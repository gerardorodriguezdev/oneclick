package theoneclick.client.core.platform

import io.ktor.client.engine.*
import io.ktor.client.engine.js.*
import kotlinx.browser.window
import theoneclick.client.core.buildkonfig.BuildKonfig
import theoneclick.client.core.extensions.toStartingRoute
import theoneclick.client.core.extensions.urlProtocol
import theoneclick.shared.core.idlingResources.EmptyIdlingResource
import theoneclick.shared.core.idlingResources.IdlingResource
import theoneclick.shared.core.routes.AppRoute
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.dispatchers.platform.dispatchersProvider
import theoneclick.shared.timeProvider.SystemTimeProvider
import theoneclick.shared.timeProvider.TimeProvider

class WasmAppDependencies(
    override val startingRoute: AppRoute,
) : AppDependencies {
    override val environment: Environment =
        Environment(
            protocol = BuildKonfig.urlProtocol(),
            host = BuildKonfig.HOST,
            port = BuildKonfig.PORT,
            isDebug = BuildKonfig.IS_DEBUG,
        )
    override val dispatchersProvider: DispatchersProvider = dispatchersProvider()
    override val httpEngine: HttpClientEngine = Js.create()
    override val idlingResource: IdlingResource = EmptyIdlingResource()
    override val timeProvider: TimeProvider = SystemTimeProvider()
}

actual fun appDependencies(): AppDependencies =
    WasmAppDependencies(startingRoute = window.location.toStartingRoute())
