package theoneclick.client.core.platform

import io.ktor.http.*

data class Environment(
    val protocol: URLProtocol?,
    val host: String?,
    val port: Int?,
    val isDebug: Boolean,
)
