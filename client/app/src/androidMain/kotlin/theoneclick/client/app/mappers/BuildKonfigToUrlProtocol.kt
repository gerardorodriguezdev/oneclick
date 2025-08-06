package theoneclick.client.app.mappers

import io.ktor.http.*
import theoneclick.client.app.buildkonfig.BuildKonfig

internal fun BuildKonfig.urlProtocol(): URLProtocol? =
    when (PROTOCOL) {
        "http" -> URLProtocol.HTTP
        "https" -> URLProtocol.HTTPS
        else -> null
    }
