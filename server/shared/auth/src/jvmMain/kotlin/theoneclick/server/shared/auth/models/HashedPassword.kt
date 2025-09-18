package theoneclick.server.shared.auth.models

import dev.drewhamilton.poko.Poko
import kotlinx.serialization.Serializable

@Serializable
@Poko
class HashedPassword private constructor(val value: String) {
    companion object {
        private const val ERROR_MESSAGE = "Invalid hashed password"

        private val REGEX = "^\$2[ayb]\$[0-9]{2}\$[A-Za-z0-9./]{53}$".toRegex()

        private fun isValid(value: String): Boolean = REGEX.matches(value)

        fun unsafe(value: String): HashedPassword = HashedPassword(value)
    }
}
