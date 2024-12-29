package theoneclick.client.core.platform

import io.ktor.client.*
import io.ktor.client.engine.*
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import theoneclick.client.core.routes.NavigationController
import theoneclick.client.core.routes.RealNavigationController
import theoneclick.shared.core.extensions.defaultHttpClient
import theoneclick.shared.core.idlingResources.IdlingResource
import theoneclick.shared.core.routes.AppRoute
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.timeProvider.TimeProvider

interface AppDependencies : CoreDependencies, HomeDependencies {
    val timeProvider: TimeProvider
}

expect fun appDependencies(): AppDependencies

interface CoreDependencies {
    val environment: Environment
    val dispatchersProvider: DispatchersProvider
    val startingRoute: AppRoute
    val httpEngine: HttpClientEngine
    val idlingResource: IdlingResource
}

fun buildCoreModule(coreDependencies: CoreDependencies): Module =
    module {
        single<DispatchersProvider> { coreDependencies.dispatchersProvider }
        single<IdlingResource> { coreDependencies.idlingResource }
        single<HttpClient> {
            defaultHttpClient(
                engine = coreDependencies.httpEngine,
                protocol = coreDependencies.environment.protocol,
                host = coreDependencies.environment.host,
                port = coreDependencies.environment.port,
            )
        }
        single { RealNavigationController() } bind NavigationController::class
    }
