package theoneclick.server.shared.models

import dev.drewhamilton.poko.Poko
import kotlinx.serialization.Serializable

@Serializable
@Poko
class HashedPassword private constructor(val value: String) {
    companion object {
        fun unsafe(value: String): HashedPassword = HashedPassword(value)
    }
}
