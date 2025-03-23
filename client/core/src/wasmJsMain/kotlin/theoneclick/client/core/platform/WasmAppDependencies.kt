package theoneclick.client.core.platform

import io.ktor.client.*
import theoneclick.client.core.navigation.NavigationController
import theoneclick.client.core.navigation.RealNavigationController
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.dispatchers.platform.dispatchersProvider

class WasmAppDependencies : AppDependencies {
    override val navigationController: NavigationController = RealNavigationController()
    override val dispatchersProvider: DispatchersProvider = dispatchersProvider()
    override val httpClient: HttpClient = wasmHttpClient(navigationController)
    override val authenticationDataSource: AuthenticationDataSource =
        WasmRemoteAuthenticationDataSource(httpClient, dispatchersProvider)
}