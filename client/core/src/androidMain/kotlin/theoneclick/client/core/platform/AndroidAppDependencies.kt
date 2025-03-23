package theoneclick.client.core.platform

import io.ktor.client.*
import io.ktor.client.engine.*
import theoneclick.client.core.dataSources.AndroidLocalTokenDataSource
import theoneclick.client.core.dataSources.TokenDataSource
import theoneclick.client.core.idlingResources.IdlingResource
import theoneclick.client.core.navigation.NavigationController
import theoneclick.client.core.navigation.RealNavigationController
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.dispatchers.platform.dispatchersProvider

class AndroidAppDependencies(
    httpClientEngine: HttpClientEngine,
    idlingResource: IdlingResource,
) : AppDependencies {
    val tokenDataSource: TokenDataSource = AndroidLocalTokenDataSource()

    override val navigationController: NavigationController = RealNavigationController()
    override val dispatchersProvider: DispatchersProvider = dispatchersProvider()
    override val httpClient: HttpClient = androidHttpClient(
        httpClientEngine = httpClientEngine,
        tokenDataSource = tokenDataSource,
        idlingResource = idlingResource,
        navigationController = navigationController,
    )
    override val authenticationDataSource: AuthenticationDataSource =
        AndroidRemoteAuthenticationDataSource(httpClient, dispatchersProvider, tokenDataSource)
}