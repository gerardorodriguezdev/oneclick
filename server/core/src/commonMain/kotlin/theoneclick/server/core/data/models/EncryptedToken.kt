package theoneclick.server.core.data.models

import kotlinx.serialization.Serializable
import theoneclick.server.core.platform.SecurityUtils

@Serializable
data class EncryptedToken(
    val value: String,
    val creationTimeInMillis: Long,
) {
    companion object {
        fun SecurityUtils.create(
            value: String,
            creationTimeInMillis: Long,
        ): EncryptedToken =
            EncryptedToken(
                value = value,
                creationTimeInMillis = creationTimeInMillis,
            )
    }
}
