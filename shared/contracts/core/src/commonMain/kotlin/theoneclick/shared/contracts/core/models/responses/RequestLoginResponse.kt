package theoneclick.shared.contracts.core.models.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.Jwt

@Serializable
data class RequestLoginResponse(val jwt: Jwt)
