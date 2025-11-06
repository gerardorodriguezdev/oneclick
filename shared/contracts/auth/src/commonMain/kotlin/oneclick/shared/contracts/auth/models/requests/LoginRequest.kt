package oneclick.shared.contracts.auth.models.requests

import kotlinx.serialization.Serializable
import oneclick.shared.contracts.auth.models.Password
import oneclick.shared.contracts.auth.models.Username
import oneclick.shared.contracts.core.models.Uuid

sealed interface LoginRequest {
    val username: Username
    val password: Password

    @Serializable
    data class UserRequestLoginRequest(
        override val username: Username,
        override val password: Password,
    ) : LoginRequest

    @Serializable
    data class HomeRequestLoginRequest(
        override val username: Username,
        override val password: Password,
        val homeId: Uuid,
    ) : LoginRequest
}