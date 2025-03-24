package theoneclick.client.core.testing

import io.ktor.client.*
import io.ktor.client.engine.*
import theoneclick.client.core.dataSources.AndroidInMemoryTokenDataSource
import theoneclick.client.core.navigation.RealNavigationController
import theoneclick.client.core.platform.androidHttpClient

fun fakeHttpClient(httpClientEngine: HttpClientEngine): HttpClient =
    androidHttpClient(
        httpClientEngine = httpClientEngine,
        tokenDataSource = AndroidInMemoryTokenDataSource(),
        navigationController = RealNavigationController(),
    )