package theoneclick.client.app.di

import io.ktor.client.*
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import theoneclick.client.app.navigation.NavigationController
import theoneclick.client.app.platform.AppDependencies
import theoneclick.client.app.platform.AuthenticationDataSource
import theoneclick.client.app.platform.LogoutManager
import theoneclick.shared.core.platform.AppLogger
import theoneclick.shared.dispatchers.platform.DispatchersProvider

@Component
abstract class CoreComponent(appDependencies: AppDependencies) : AppDependencies {

    @get:Provides
    override val appLogger: AppLogger = appDependencies.appLogger

    @get:Provides
    override val navigationController: NavigationController = appDependencies.navigationController

    @get:Provides
    override val dispatchersProvider: DispatchersProvider = appDependencies.dispatchersProvider

    @get:Provides
    override val httpClient: HttpClient = appDependencies.httpClient

    @get:Provides
    override val authenticationDataSource: AuthenticationDataSource = appDependencies.authenticationDataSource

    @get:Provides
    override val logoutManager: LogoutManager = appDependencies.logoutManager
}
