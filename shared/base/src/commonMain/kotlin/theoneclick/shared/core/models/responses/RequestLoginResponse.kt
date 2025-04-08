package theoneclick.shared.core.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class RequestLoginResponse(val token: String)
