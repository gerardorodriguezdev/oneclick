package oneclick.client.shared.di

import io.ktor.client.engine.*
import io.ktor.http.*
import oneclick.client.shared.navigation.NavigationController
import oneclick.client.shared.network.platform.LogoutManager
import oneclick.client.shared.network.platform.RemoteAuthenticationDataSource
import oneclick.client.shared.notifications.NotificationsController
import oneclick.shared.contracts.core.models.ClientType
import oneclick.shared.dispatchers.platform.DispatchersProvider
import oneclick.shared.logging.AppLogger
import oneclick.shared.network.dataSources.TokenDataSource
import oneclick.shared.network.nativeHttpClient

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
