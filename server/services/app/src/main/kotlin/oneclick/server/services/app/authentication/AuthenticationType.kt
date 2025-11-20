package oneclick.server.services.app.authentication

internal enum class AuthenticationType(val value: String) {
    USER_SESSION("user_session_authentication"),
    USER_JWT("user_jwt_authentication"),
    HOME_JWT("home_jwt_authentication")
}