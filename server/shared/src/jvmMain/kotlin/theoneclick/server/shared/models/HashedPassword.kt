package theoneclick.server.shared.models

import kotlinx.serialization.Serializable
import theoneclick.server.shared.security.Encryptor

@Serializable
@JvmInline
value class HashedPassword(val value: String) {
    companion object {
        fun Encryptor.create(value: String): HashedPassword = HashedPassword(value)
    }
}
