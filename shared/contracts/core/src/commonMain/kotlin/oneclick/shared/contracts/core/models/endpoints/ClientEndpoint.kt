package oneclick.shared.contracts.core.models.endpoints

enum class ClientEndpoint(val route: String) {
    IS_USER_LOGGED("/api/is-user-logged"),
    REQUEST_LOGIN("/api/request-login"),
    HOMES("/api/homes-list"),
    LOGOUT("/api/logout"),
    SAVE_DEVICE("/api/save-device"),
}
