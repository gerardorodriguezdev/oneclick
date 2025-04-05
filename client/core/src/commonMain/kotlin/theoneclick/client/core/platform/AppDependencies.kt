package theoneclick.client.core.platform

import io.ktor.client.*
import theoneclick.client.core.navigation.NavigationController
import theoneclick.shared.core.platform.AppLogger
import theoneclick.shared.dispatchers.platform.DispatchersProvider

interface AppDependencies {
    val appLogger: AppLogger
    val navigationController: NavigationController
    val dispatchersProvider: DispatchersProvider
    val httpClient: HttpClient
    val authenticationDataSource: AuthenticationDataSource
    val logoutManager: LogoutManager
}