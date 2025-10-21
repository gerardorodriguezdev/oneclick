package oneclick.client.shared.di

import io.ktor.client.engine.*
import io.ktor.http.*
import oneclick.client.shared.navigation.NavigationController
import oneclick.client.shared.network.dataSources.TokenDataSource
import oneclick.client.shared.network.IOSRemoteAuthenticationDataSource
import oneclick.client.shared.network.platform.LogoutManager
import oneclick.client.shared.network.iosHttpClient
import oneclick.client.shared.notifications.NotificationsController
import oneclick.shared.dispatchers.platform.DispatchersProvider
import oneclick.shared.logging.AppLogger

fun iosCoreComponent(
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
    val httpClient = iosHttpClient(
        urlProtocol = urlProtocol,
        host = host,
        port = port,
        appLogger = appLogger,
        httpClientEngine = httpClientEngine,
        tokenDataSource = tokenDataSource,
        logoutManager = logoutManager,
    )

    return createCoreComponent(
        appLogger = appLogger,
        dispatchersProvider = dispatchersProvider,
        navigationController = navigationController,
        httpClient = httpClient,
        authenticationDataSource = IOSRemoteAuthenticationDataSource(
            httpClient,
            dispatchersProvider,
            tokenDataSource,
            appLogger
        ),
        notificationsController = notificationsController,
    )
}
