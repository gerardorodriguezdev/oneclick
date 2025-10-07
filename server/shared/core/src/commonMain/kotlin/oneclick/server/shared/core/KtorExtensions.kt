package oneclick.server.shared.core

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import oneclick.shared.contracts.core.models.ClientType
import oneclick.shared.contracts.core.models.ClientType.Companion.toClientType
import oneclick.shared.network.ClientType

val RoutingRequest.clientType: ClientType
    get() {
        return call.request.clientType()
    }

fun ApplicationRequest.clientType(): ClientType = header(HttpHeaders.ClientType).toClientType()