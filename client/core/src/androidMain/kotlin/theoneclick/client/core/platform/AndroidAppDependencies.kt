package theoneclick.client.core.platform

import io.ktor.client.*
import io.ktor.client.engine.*
import theoneclick.client.core.dataSources.TokenDataSource
import theoneclick.client.core.navigation.NavigationController
import theoneclick.client.core.navigation.RealNavigationController
import theoneclick.shared.dispatchers.platform.DispatchersProvider

class AndroidAppDependencies(
    httpClientEngine: HttpClientEngine,
    tokenDataSource: TokenDataSource,
    override val dispatchersProvider: DispatchersProvider,
) : AppDependencies {
    override val navigationController: NavigationController = RealNavigationController()
    override val httpClient: HttpClient = androidHttpClient(
        httpClientEngine = httpClientEngine,
        tokenDataSource = tokenDataSource,
        navigationController = navigationController,
    )
    override val authenticationDataSource: AuthenticationDataSource =
        AndroidRemoteAuthenticationDataSource(httpClient, dispatchersProvider, tokenDataSource)
}