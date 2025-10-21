package oneclick.client.shared.network

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*

fun iosHttpClientEngine(): HttpClientEngine =
    Darwin.create {
        configureRequest {
            setAllowsCellularAccess(true)
        }
    }
