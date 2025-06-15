package theoneclick.server.app.models

import kotlinx.serialization.Serializable
import theoneclick.server.app.security.Encryptor

@Serializable
class EncryptedToken private constructor(
    val token: String,
    val creationTimeInMillis: Long,
) {
    companion object {
        fun Encryptor.create(
            token: String,
            creationTimeInMillis: Long,
        ): EncryptedToken =
            EncryptedToken(
                token = token,
                creationTimeInMillis = creationTimeInMillis,
            )
    }
}
