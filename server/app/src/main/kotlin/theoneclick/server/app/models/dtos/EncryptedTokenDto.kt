package theoneclick.server.app.models.dtos

import kotlinx.serialization.Serializable
import theoneclick.server.app.security.Encryptor
import theoneclick.shared.contracts.core.dtos.TokenDto

@Serializable
class EncryptedTokenDto private constructor(
    val token: TokenDto,
    val creationTimeInMillis: Long,
) {
    companion object {
        fun Encryptor.create(
            token: String,
            creationTimeInMillis: Long,
        ): EncryptedTokenDto =
            EncryptedTokenDto(
                token = TokenDto.unsafe(token),
                creationTimeInMillis = creationTimeInMillis,
            )
    }
}
