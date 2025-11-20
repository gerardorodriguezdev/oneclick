package oneclick.client.apps.user.di

import io.ktor.client.*
import io.ktor.client.engine.*
import oneclick.client.apps.user.navigation.NavigationController
import oneclick.client.shared.network.platform.LogoutManager
import oneclick.client.shared.network.platform.WasmRemoteAuthenticationDataSource
import oneclick.client.shared.network.platform.wasmHttpClient
import oneclick.client.apps.user.notifications.NotificationsController
import oneclick.shared.dispatchers.platform.DispatchersProvider
import oneclick.shared.logging.AppLogger

fun wasmCoreComponent(
    httpClientEngine: HttpClientEngine,
    appLogger: AppLogger,
    dispatchersProvider: DispatchersProvider,
    navigationController: NavigationController,
    logoutManager: LogoutManager,
    notificationsController: NotificationsController,
): CoreComponent {
    val httpClient: HttpClient = wasmHttpClient(
        httpClientEngine = httpClientEngine,
        logoutManager = logoutManager,
    )

    return CoreComponent::class.create(
        appLogger = appLogger,
        navigationController = navigationController,
        dispatchersProvider = dispatchersProvider,
        httpClient = httpClient,
        authenticationDataSource = WasmRemoteAuthenticationDataSource(httpClient, dispatchersProvider, appLogger),
        notificationsController = notificationsController,
    )
}
