package oneclick.shared.contracts.auth.models.requests

import kotlinx.serialization.Serializable
import oneclick.shared.contracts.auth.models.Password
import oneclick.shared.contracts.auth.models.Username
import oneclick.shared.contracts.core.models.Uuid

@Serializable
data class HomeRequestLoginRequest(
    val username: Username,
    val password: Password,
    val homeId: Uuid,
)