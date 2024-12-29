package theoneclick.server.core.endpoints.tokenExchange

data class TokenExchangeParams(
    val clientId: String?,
    val clientSecret: String?,
    val grantType: String?,
    val authorizationCode: String?,
    val redirectUri: String?,
    val refreshToken: String?,
) {
    companion object {
        const val AUTHORIZATION_CODE_TYPE = "authorization_code"
        const val REFRESH_TOKEN_TYPE = "refresh_token"
    }
}
