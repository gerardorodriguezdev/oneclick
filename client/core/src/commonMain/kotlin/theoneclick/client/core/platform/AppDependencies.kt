package theoneclick.client.core.platform

import io.ktor.client.*
import theoneclick.client.core.navigation.NavigationController
import theoneclick.shared.dispatchers.platform.DispatchersProvider

interface AppDependencies {
    val navigationController: NavigationController
    val dispatchersProvider: DispatchersProvider
    val httpClient: HttpClient
    val authenticationDataSource: AuthenticationDataSource
}