package theoneclick.shared.core.models.endpoints

import theoneclick.shared.core.models.endpoints.base.Endpoint

enum class ClientEndpoint(override val route: String) : Endpoint {
    IS_USER_LOGGED("/api/is-user-logged"),
    REQUEST_LOGIN("/api/request-login"),
    ADD_DEVICE("/api/add-device"),
    DEVICES("/api/devices"),
    UPDATE_DEVICE("/api/update-device"),
    LOGOUT("/api/logout"),
}