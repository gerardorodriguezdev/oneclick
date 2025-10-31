package oneclick.client.shared.network.platform

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import okhttp3.OkHttpClient

fun okhttpHttpClientEngine(block: OkHttpClient.Builder.() -> Unit = {}): HttpClientEngine =
    OkHttp.create {
        config {
            followRedirects(false)
            block()
        }
    }
