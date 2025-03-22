package theoneclick.client.core.platform

import io.ktor.http.*

data class Environment(
    val urlProtocol: URLProtocol?,
    val isDebug: Boolean,
)
