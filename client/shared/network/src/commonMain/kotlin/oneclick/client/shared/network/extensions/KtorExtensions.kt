package oneclick.client.shared.network.extensions

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMessageBuilder
import oneclick.shared.contracts.core.models.ClientType
import oneclick.shared.network.ClientType

fun HttpMessageBuilder.clientType(clientType: ClientType): Unit = headers.set(HttpHeaders.ClientType, clientType.value)