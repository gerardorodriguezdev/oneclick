package theoneclick.client.core.platform

import io.ktor.client.*
import io.ktor.client.engine.*
import theoneclick.client.core.navigation.NavigationController
import theoneclick.shared.dispatchers.platform.DispatchersProvider

class WasmAppDependencies(
    httpClientEngine: HttpClientEngine,
    override val dispatchersProvider: DispatchersProvider,
    override val navigationController: NavigationController,
) : AppDependencies {
    override val httpClient: HttpClient = wasmHttpClient(
        httpClientEngine = httpClientEngine,
        navigationController = navigationController
    )
    override val authenticationDataSource: AuthenticationDataSource =
        WasmRemoteAuthenticationDataSource(httpClient, dispatchersProvider)
}