package theoneclick.server.shared.extensions

import io.ktor.server.request.*
import io.ktor.server.routing.*
import theoneclick.shared.core.models.agents.Agent
import theoneclick.shared.core.models.agents.Agent.Companion.toAgent

val RoutingRequest.agent: Agent
    get() {
        val userAgent = userAgent()
        return userAgent.toAgent()
    }