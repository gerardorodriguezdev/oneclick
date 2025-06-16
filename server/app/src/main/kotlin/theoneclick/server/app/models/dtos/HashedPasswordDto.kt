package theoneclick.server.app.models.dtos

import kotlinx.serialization.Serializable
import theoneclick.server.app.security.Encryptor

@Serializable
@JvmInline
value class HashedPasswordDto(val value: String) {
    companion object {
        fun Encryptor.create(value: String): HashedPasswordDto = HashedPasswordDto(value)
    }
}
