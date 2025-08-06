package theoneclick.client.shared.di

import io.ktor.client.engine.*
import io.ktor.http.*
import theoneclick.client.shared.navigation.NavigationController
import theoneclick.client.shared.network.dataSources.TokenDataSource
import theoneclick.client.shared.network.platform.IOSRemoteAuthenticationDataSource
import theoneclick.client.shared.network.platform.LogoutManager
import theoneclick.client.shared.network.platform.iosHttpClient
import theoneclick.client.shared.notifications.NotificationsController
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.logging.AppLogger

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
