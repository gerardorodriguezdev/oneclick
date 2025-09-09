package theoneclick.shared.contracts.auth.models.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.auth.models.Jwt

@Serializable
data class RequestLoginResponse(val jwt: Jwt)