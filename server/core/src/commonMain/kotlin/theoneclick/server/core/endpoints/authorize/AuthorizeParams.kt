package theoneclick.server.core.endpoints.authorize

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizeParams(
    val state: String?,
    val clientId: String?,
    val redirectUri: String?,
    val responseType: String?,
) {
    companion object {
        const val RESPONSE_TYPE_CODE = "code"
    }
}
