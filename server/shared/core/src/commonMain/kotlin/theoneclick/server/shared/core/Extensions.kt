package theoneclick.server.shared.core

import io.ktor.server.request.*
import io.ktor.server.routing.*
import theoneclick.shared.contracts.core.models.agents.Agent
import theoneclick.shared.contracts.core.models.agents.Agent.Companion.toAgent

val RoutingRequest.agent: Agent
    get() {
        val userAgent = userAgent()
        return userAgent.toAgent()
    }