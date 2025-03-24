package theoneclick.client.core.testing.fakes

import io.ktor.client.*
import theoneclick.client.core.dataSources.AndroidInMemoryTokenDataSource
import theoneclick.client.core.navigation.NavigationController
import theoneclick.client.core.platform.androidHttpClient
import theoneclick.shared.core.models.entities.Device

fun fakeAndroidHttpClient(
    isUserLogged: () -> Boolean = { false },
    devices: () -> List<Device> = { emptyList() },
    navigationController: NavigationController,
): HttpClient =
    androidHttpClient(
        httpClientEngine = fakeHttpClientEngine(
            isUserLogged = isUserLogged,
            devices = devices,
        ),
        tokenDataSource = AndroidInMemoryTokenDataSource(),
        navigationController = navigationController,
    )