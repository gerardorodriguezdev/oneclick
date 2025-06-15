package theoneclick.server.app.models

import kotlinx.serialization.Serializable
import theoneclick.server.app.security.Encryptor

@Serializable
@JvmInline
value class HashedPassword(val value: String) {
    companion object {
        fun Encryptor.create(value: String): HashedPassword = HashedPassword(value)
    }
}
