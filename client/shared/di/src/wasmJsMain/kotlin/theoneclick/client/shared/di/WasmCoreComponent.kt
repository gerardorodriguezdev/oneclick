package theoneclick.client.shared.di

import io.ktor.client.*
import io.ktor.client.engine.*
import theoneclick.client.shared.navigation.NavigationController
import theoneclick.client.shared.network.platform.LogoutManager
import theoneclick.client.shared.network.platform.WasmRemoteAuthenticationDataSource
import theoneclick.client.shared.network.platform.wasmHttpClient
import theoneclick.shared.core.platform.AppLogger
import theoneclick.shared.dispatchers.platform.DispatchersProvider

fun wasmCoreComponent(
    httpClientEngine: HttpClientEngine,
    appLogger: AppLogger,
    dispatchersProvider: DispatchersProvider,
    navigationController: NavigationController,
    logoutManager: LogoutManager,
): CoreComponent {
    val httpClient: HttpClient = wasmHttpClient(
        httpClientEngine = httpClientEngine,
        logoutManager = logoutManager,
    )

    return CoreComponent::class.create(
        appLogger = appLogger,
        navigationController = navigationController,
        dispatchersProvider = dispatchersProvider,
        logoutManager = logoutManager,
        httpClient = httpClient,
        authenticationDataSource = WasmRemoteAuthenticationDataSource(httpClient, dispatchersProvider, appLogger),
    )
}