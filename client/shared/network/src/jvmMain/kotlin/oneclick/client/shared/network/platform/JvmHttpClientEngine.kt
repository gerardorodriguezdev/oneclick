package oneclick.client.shared.network.platform

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import oneclick.shared.timeProvider.TimeProvider

fun jvmHttpClientEngine(timeProvider: TimeProvider): HttpClientEngine =
    OkHttp.create {
        config {
            followRedirects(false)
        }
    }
