package theoneclick.server.shared.models

import kotlinx.serialization.Serializable
import theoneclick.server.shared.security.Encryptor
import theoneclick.shared.contracts.core.models.NonNegativeLong
import theoneclick.shared.contracts.core.models.Token

@Serializable
class EncryptedToken private constructor(
    val token: Token,
    val creationTimeInMillis: NonNegativeLong,
) {
    companion object {
        fun Encryptor.create(
            token: String,
            creationTimeInMillis: Long,
        ): EncryptedToken =
            EncryptedToken(
                token = Token.unsafe(token),
                creationTimeInMillis = NonNegativeLong.unsafe(creationTimeInMillis),
            )
    }
}
