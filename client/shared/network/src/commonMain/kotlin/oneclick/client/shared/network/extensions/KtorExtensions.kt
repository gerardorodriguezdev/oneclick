package oneclick.client.shared.network.extensions

import io.ktor.http.*
import oneclick.shared.contracts.core.models.ClientType
import oneclick.shared.network.ClientType

fun HttpMessageBuilder.clientType(clientType: ClientType): Unit = headers.set(HttpHeaders.ClientType, clientType.value)

fun String.urlProtocol(): URLProtocol? =
    when (this) {
        "http" -> URLProtocol.HTTP
        "https" -> URLProtocol.HTTPS
        else -> null
    }
