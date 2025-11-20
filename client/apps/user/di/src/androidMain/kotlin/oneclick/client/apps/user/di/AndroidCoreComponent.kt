package oneclick.client.apps.user.di

import io.ktor.client.engine.*
import io.ktor.http.*
import oneclick.client.apps.user.navigation.NavigationController
import oneclick.client.apps.user.notifications.NotificationsController
import oneclick.client.shared.network.dataSources.RemoteAuthenticationDataSource
import oneclick.client.shared.network.dataSources.TokenDataSource
import oneclick.client.shared.network.nativeHttpClient
import oneclick.client.shared.network.platform.LogoutManager
import oneclick.shared.contracts.core.models.ClientType
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
    val httpClient = nativeHttpClient(
        urlProtocol = urlProtocol,
        host = host,
        port = port,
        clientType = ClientType.MOBILE,
        appLogger = appLogger,
        httpClientEngine = httpClientEngine,
        tokenDataSource = tokenDataSource,
        logoutManager = logoutManager
    )

    return CoreComponent::class.create(
        appLogger = appLogger,
        dispatchersProvider = dispatchersProvider,
        navigationController = navigationController,
        httpClient = httpClient,
        authenticationDataSource = RemoteAuthenticationDataSource(
            httpClient,
            dispatchersProvider,
            tokenDataSource,
            appLogger
        ),
        notificationsController = notificationsController,
    )
}
