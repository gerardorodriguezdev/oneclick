package oneclick.shared.contracts.auth.models.requests

import kotlinx.serialization.Serializable
import oneclick.shared.contracts.auth.models.Password
import oneclick.shared.contracts.auth.models.Username

@Serializable
data class UserRequestLoginRequest(
    val username: Username,
    val password: Password,
)