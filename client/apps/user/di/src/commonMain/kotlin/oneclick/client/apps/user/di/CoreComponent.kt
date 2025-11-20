package oneclick.client.apps.user.di

import io.ktor.client.*
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides
import oneclick.client.apps.user.navigation.NavigationController
import oneclick.client.shared.network.platform.AuthenticationDataSource
import oneclick.client.apps.user.notifications.NotificationsController
import oneclick.shared.dispatchers.platform.DispatchersProvider
import oneclick.shared.logging.AppLogger

@Component
abstract class CoreComponent(
    @get:Provides
    val appLogger: AppLogger,

    @get:Provides
    val navigationController: NavigationController,

    @get:Provides
    val dispatchersProvider: DispatchersProvider,

    @get:Provides
    val httpClient: HttpClient,

    @get:Provides
    val authenticationDataSource: AuthenticationDataSource,

    @get:Provides
    val notificationsController: NotificationsController,
)

@KmpComponentCreate
expect fun createCoreComponent(
    appLogger: AppLogger,
    navigationController: NavigationController,
    dispatchersProvider: DispatchersProvider,
    httpClient: HttpClient,
    authenticationDataSource: AuthenticationDataSource,
    notificationsController: NotificationsController,
): CoreComponent
