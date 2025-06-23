package theoneclick.server.app.models

import kotlinx.serialization.Serializable
import theoneclick.server.app.security.Encryptor
import theoneclick.shared.contracts.core.models.Token

@Serializable
class EncryptedToken private constructor(
    val token: Token,
    val creationTimeInMillis: Long,
) {
    companion object Companion {
        fun Encryptor.create(
            token: String,
            creationTimeInMillis: Long,
        ): EncryptedToken =
            EncryptedToken(
                token = Token.unsafe(token),
                creationTimeInMillis = creationTimeInMillis,
            )
    }
}
