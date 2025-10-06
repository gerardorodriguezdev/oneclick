package oneclick.client.shared.di

import io.ktor.client.engine.*
import io.ktor.http.*
import oneclick.client.shared.navigation.NavigationController
import oneclick.client.shared.network.dataSources.TokenDataSource
import oneclick.client.shared.network.platform.AndroidRemoteAuthenticationDataSource
import oneclick.client.shared.network.platform.LogoutManager
import oneclick.client.shared.network.platform.androidHttpClient
import oneclick.client.shared.notifications.NotificationsController
import oneclick.shared.dispatchers.platform.DispatchersProvider
import oneclick.shared.logging.AppLogger

fun androidCoreComponent(
    urlProtocol: URLProtocol?,
    host: String?,
    port: Int?,
    httpClientEngine: HttpClientEngine,
    tokenDataSource: TokenDataSource,
    appLogger: AppLogger,
    dispatchersProvider: DispatchersProvider,
    navigationController: NavigationController,
    logoutManager: LogoutManager,
    notificationsController: NotificationsController,
): CoreComponent {
    val httpClient = androidHttpClient(
        urlProtocol = urlProtocol,
        host = host,
        port = port,
        appLogger = appLogger,
        httpClientEngine = httpClientEngine,
        tokenDataSource = tokenDataSource,
        logoutManager = logoutManager,
    )

    return CoreComponent::class.create(
        appLogger = appLogger,
        dispatchersProvider = dispatchersProvider,
        navigationController = navigationController,
        httpClient = httpClient,
        authenticationDataSource = AndroidRemoteAuthenticationDataSource(
            httpClient,
            dispatchersProvider,
            tokenDataSource,
            appLogger
        ),
        notificationsController = notificationsController,
    )
}
