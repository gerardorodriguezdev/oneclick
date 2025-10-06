package oneclick.shared.contracts.auth.models.requests

import kotlinx.serialization.Serializable
import oneclick.shared.contracts.auth.models.Password
import oneclick.shared.contracts.auth.models.Username

@Serializable
data class RequestLoginRequest(
    val username: Username,
    val password: Password,
)