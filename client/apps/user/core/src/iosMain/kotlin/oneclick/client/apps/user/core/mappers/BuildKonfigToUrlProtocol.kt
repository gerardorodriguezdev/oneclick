package oneclick.client.apps.user.core.mappers

import io.ktor.http.*
import oneclick.client.apps.user.core.buildkonfig.BuildKonfig

internal fun BuildKonfig.urlProtocol(): URLProtocol? =
    when (PROTOCOL) {
        "http" -> URLProtocol.HTTP
        "https" -> URLProtocol.HTTPS
        else -> null
    }
