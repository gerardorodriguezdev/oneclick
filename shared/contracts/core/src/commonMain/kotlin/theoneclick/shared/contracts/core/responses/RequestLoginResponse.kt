package theoneclick.shared.contracts.core.responses

import kotlinx.serialization.Serializable

@Serializable
data class RequestLoginResponse(val token: String)
