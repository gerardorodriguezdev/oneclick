package theoneclick.server.core.endpoints.tokenExchange

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import theoneclick.server.core.validators.ParamsValidator

@Serializable
data class TokenExchangeResponse(
    @SerialName("token_type")
    val tokenType: String = TOKEN_TYPE,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String?,
    @SerialName("expires_in")
    val expiresIn: Long = ParamsValidator.ACCESS_TOKEN_EXPIRATION_IN_MILLIS,
) {
    companion object {
        const val TOKEN_TYPE = "Bearer"
    }
}
