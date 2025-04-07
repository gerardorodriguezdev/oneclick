package theoneclick.client.core.platform

import io.ktor.client.*
import io.ktor.client.engine.*
import theoneclick.client.core.dataSources.TokenDataSource
import theoneclick.client.core.navigation.NavigationController
import theoneclick.shared.core.platform.AppLogger
import theoneclick.shared.dispatchers.platform.DispatchersProvider

class AndroidAppDependencies(
    httpClientEngine: HttpClientEngine,
    private val tokenDataSource: TokenDataSource,
    override val appLogger: AppLogger,
    override val dispatchersProvider: DispatchersProvider,
    override val navigationController: NavigationController,
    override val logoutManager: LogoutManager,
) : AppDependencies {
    override val httpClient: HttpClient = androidHttpClient(
        appLogger = appLogger,
        httpClientEngine = httpClientEngine,
        tokenDataSource = tokenDataSource,
        logoutManager = logoutManager,
    )
    override val authenticationDataSource: AuthenticationDataSource =
        AndroidRemoteAuthenticationDataSource(httpClient, dispatchersProvider, tokenDataSource, appLogger)
}
