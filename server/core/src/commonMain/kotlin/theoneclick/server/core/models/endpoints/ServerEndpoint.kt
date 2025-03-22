package theoneclick.server.core.models.endpoints

import theoneclick.shared.core.models.endpoints.base.Endpoint

enum class ServerEndpoint(override val route: String) : Endpoint {
    // Apis
    HEALTHZ("/api/healthz"),

    // Qaapis
    ADD_USER_SESSION("/qaapi/add-user-session"),
    ADD_USER_DATA("/qaapi/add-user-data"),
}