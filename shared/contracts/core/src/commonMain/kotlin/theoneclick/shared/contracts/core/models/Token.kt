package theoneclick.shared.contracts.core.models

import dev.drewhamilton.poko.Poko
import kotlinx.serialization.Serializable

@Poko
@Serializable
class Token private constructor(val value: String) {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    companion object {
        private const val ERROR_MESSAGE = "Invalid token"

        private val REGEX = "^[A-Za-z0-9+/]+={0,2}$".toRegex()

        private fun isValid(value: String): Boolean = REGEX.matches(value)

        fun String.toToken(): Token? =
            if (isValid(this)) Token(this) else null

        fun unsafe(value: String): Token = Token(value)
    }
}
