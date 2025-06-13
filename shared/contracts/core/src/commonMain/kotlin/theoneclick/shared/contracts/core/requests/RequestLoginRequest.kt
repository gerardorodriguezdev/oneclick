package theoneclick.shared.contracts.core.requests

import kotlinx.serialization.Serializable

@Serializable
data class RequestLoginRequest(
    val username: String,
    val password: String,
)
