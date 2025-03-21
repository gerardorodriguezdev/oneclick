package theoneclick.server.core.data.models.endpoints

import theoneclick.shared.core.models.endpoints.base.Endpoint

enum class ServerEndpoints(override val route: String) : Endpoint {
    // Apis
    TOKEN_EXCHANGE("/api/token-exchange"),
    FULFILLMENT("/api/fulfillment"),
    AUTHORIZE("/api/authorize"),
    HEALTHZ("/api/healthz"),

    // Qaapis
    ADD_USER_SESSION("/qaapi/add-user-session"),
    ADD_AUTHORIZE_REDIRECT("/qaapi/add-authorize-redirect"),
    ADD_USER_DATA("/qaapi/add-user-data"),

    // Pages
    LOGIN("/login"),
    HOME("/home"),
    INDEX("/"),
}