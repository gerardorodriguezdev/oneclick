package theoneclick.shared.contracts.core.models.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.Token

@Serializable
data class RequestLoginResponse(val token: Token)
