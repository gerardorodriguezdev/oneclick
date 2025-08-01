package theoneclick.server.shared.models

import dev.drewhamilton.poko.Poko
import kotlinx.serialization.Serializable
import theoneclick.server.shared.security.Encryptor

@Serializable
@Poko
class HashedPassword private constructor(val value: String) {
    companion object {
        fun Encryptor.create(value: String): HashedPassword = HashedPassword(value)

        fun unsafe(value: String): HashedPassword = HashedPassword(value)
    }
}
