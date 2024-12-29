package theoneclick.shared.core.dataSources.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class RequestLoginRequest(
    val username: String,
    val password: String,
)
