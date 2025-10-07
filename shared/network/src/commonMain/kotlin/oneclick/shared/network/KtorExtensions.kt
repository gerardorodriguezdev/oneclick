package oneclick.shared.network

import io.ktor.http.*

val HttpHeaders.ClientType: String
    get() = "X-Client-Type"