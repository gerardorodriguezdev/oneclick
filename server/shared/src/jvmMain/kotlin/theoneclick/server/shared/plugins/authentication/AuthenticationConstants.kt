package theoneclick.server.shared.plugins.authentication

object AuthenticationConstants {
    const val JWT_AUTHENTICATION = "jwt_authentication"
    const val JWT_PAYLOAD_CLAIM_NAME = "payload"
    const val JWT_SESSION_NAME = "user_session"
    const val JWT_EXPIRATION_IN_MILLIS = 3_000L
}
