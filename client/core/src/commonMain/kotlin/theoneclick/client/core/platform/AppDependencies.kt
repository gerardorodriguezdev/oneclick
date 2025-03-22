package theoneclick.client.core.platform

import io.ktor.client.*
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import theoneclick.client.core.idlingResources.IdlingResource
import theoneclick.client.core.routes.NavigationController
import theoneclick.client.core.routes.RealNavigationController
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.timeProvider.TimeProvider

interface AppDependencies : CoreDependencies, HomeDependencies {
    val timeProvider: TimeProvider
}

expect fun appDependencies(): AppDependencies

interface CoreDependencies {
    val environment: Environment
    val dispatchersProvider: DispatchersProvider
    val httpClient: HttpClient
    val idlingResource: IdlingResource
}

fun buildCoreModule(coreDependencies: CoreDependencies): Module =
    module {
        single<DispatchersProvider> { coreDependencies.dispatchersProvider }
        single<IdlingResource> { coreDependencies.idlingResource }
        single<HttpClient> { coreDependencies.httpClient }
        single { RealNavigationController() } bind NavigationController::class
    }
