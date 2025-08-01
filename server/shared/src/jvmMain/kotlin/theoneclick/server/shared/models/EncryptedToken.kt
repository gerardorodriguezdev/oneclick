package theoneclick.server.shared.models

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.NonNegativeLong
import theoneclick.shared.contracts.core.models.Token

@Serializable
data class EncryptedToken(
    val token: Token,
    val creationTimeInMillis: NonNegativeLong,
)
