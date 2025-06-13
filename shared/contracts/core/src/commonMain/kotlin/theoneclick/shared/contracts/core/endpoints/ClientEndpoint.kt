package theoneclick.shared.contracts.core.endpoints

import theoneclick.shared.contracts.core.endpoints.base.Endpoint

enum class ClientEndpoint(override val route: String) : Endpoint {
    IS_USER_LOGGED("/api/is-user-logged"),
    REQUEST_LOGIN("/api/request-login"),
    HOMES("/api/homes"),
    LOGOUT("/api/logout"),
}
