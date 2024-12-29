package theoneclick.shared.core.dataSources.models.endpoints

enum class Endpoint(val route: String) {
    // Apis
    TOKEN_EXCHANGE("/api/token-exchange"),
    REQUEST_LOGIN("/api/request-login"),
    FULFILLMENT("/api/fulfillment"),
    AUTHORIZE("/api/authorize"),
    HEALTHZ("/api/healthz"),
    ADD_DEVICE("/api/add-device"),
    DEVICES("/api/devices"),
    UPDATE_DEVICE("/api/update-device"),
    IS_USER_LOGGED("/api/is-user-logged"),

    // Qaapis
    ADD_USER_SESSION("/qaapi/add-user-session"),
    ADD_AUTHORIZE_REDIRECT("/qaapi/add-authorize-redirect"),
    ADD_USER_DATA("/qaapi/add-user-data"),

    // Pages
    LOGIN("/login"),
    HOME("/home"),
    INDEX("/"),
}
