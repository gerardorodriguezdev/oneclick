package theoneclick.client.core.platform

import io.ktor.client.*
import theoneclick.client.core.dataSources.AndroidLocalTokenDataSource
import theoneclick.client.core.dataSources.TokenDataSource
import theoneclick.client.core.idlingResources.EmptyIdlingResource
import theoneclick.client.core.idlingResources.IdlingResource
import theoneclick.client.core.navigation.NavigationController
import theoneclick.client.core.navigation.RealNavigationController
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.dispatchers.platform.dispatchersProvider
import theoneclick.shared.timeProvider.SystemTimeProvider
import theoneclick.shared.timeProvider.TimeProvider

class AndroidAppDependencies : AppDependencies {
    val idlingResource: IdlingResource = EmptyIdlingResource()
    val tokenDataSource: TokenDataSource = AndroidLocalTokenDataSource()
    val timeProvider: TimeProvider = SystemTimeProvider()

    override val navigationController: NavigationController = RealNavigationController()
    override val dispatchersProvider: DispatchersProvider = dispatchersProvider()
    override val httpClient: HttpClient = androidHttpClient(
        timeProvider = timeProvider,
        tokenDataSource = tokenDataSource,
        idlingResource = idlingResource,
        navigationController = navigationController,
    )
    override val authenticationDataSource: AuthenticationDataSource =
        AndroidRemoteAuthenticationDataSource(httpClient, dispatchersProvider, tokenDataSource)
}