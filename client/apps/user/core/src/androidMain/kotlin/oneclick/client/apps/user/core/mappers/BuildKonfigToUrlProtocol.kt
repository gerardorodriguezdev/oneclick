package oneclick.client.apps.user.core.mappers

import io.ktor.http.*
import oneclick.client.apps.user.core.buildkonfig.BuildKonfig
import oneclick.client.shared.network.extensions.urlProtocol

internal fun BuildKonfig.urlProtocol(): URLProtocol? = PROTOCOL?.urlProtocol()
