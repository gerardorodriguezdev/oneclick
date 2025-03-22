package theoneclick.client.core.mappers

import io.ktor.http.*
import theoneclick.client.core.buildkonfig.BuildKonfig

internal fun BuildKonfig.urlProtocol(): URLProtocol? =
    when (PROTOCOL) {
        "http" -> URLProtocol.HTTP
        "https" -> URLProtocol.HTTPS
        else -> null
    }
