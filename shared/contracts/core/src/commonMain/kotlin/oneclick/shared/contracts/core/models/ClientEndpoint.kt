package oneclick.shared.contracts.core.models

enum class ClientEndpoint(val route: String) {
    USER_REQUEST_LOGIN("/api/user/request-login"),
    USER_HOMES("/api/user/homes"),
    USER_APPROVE_REGISTRABLE_USER("/api/user/approve-registrable-user"),

    HOME_REQUEST_LOGIN("/api/home/request-login"),
    HOME_SYNC_DEVICES("/api/home/sync-devices"),

    IS_LOGGED("/api/is-logged"),
    LOGOUT("/api/logout"),
}