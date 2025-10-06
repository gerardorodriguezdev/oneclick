package oneclick.server.shared.core

import io.ktor.server.request.*
import io.ktor.server.routing.*
import oneclick.shared.contracts.core.models.agents.Agent
import oneclick.shared.contracts.core.models.agents.Agent.Companion.toAgent

val RoutingRequest.agent: Agent
    get() {
        val userAgent = userAgent()
        return userAgent.toAgent()
    }