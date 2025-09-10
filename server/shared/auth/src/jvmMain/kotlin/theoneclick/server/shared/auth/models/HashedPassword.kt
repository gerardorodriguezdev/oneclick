package theoneclick.server.shared.auth.models

import dev.drewhamilton.poko.Poko
import kotlinx.serialization.Serializable

//TODO: Formatting here
@Serializable
@Poko
class HashedPassword private constructor(val value: String) {
    companion object {
        fun unsafe(value: String): HashedPassword = HashedPassword(value)
    }
}
