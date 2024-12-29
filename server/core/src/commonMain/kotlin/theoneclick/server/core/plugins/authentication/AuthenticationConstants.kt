package theoneclick.server.core.plugins.authentication

object AuthenticationConstants {
    const val SESSION_AUTHENTICATION = "auth_session"
    const val BEARER_AUTHENTICATION = "auth_bearer"

    const val USER_SESSION_NAME = "user_session"
    const val AUTHORIZE_REDIRECT_SESSION = "authorize_redirect_session"

    const val COOKIE_SESSION_DURATION_IN_SECONDS = 60L
}
