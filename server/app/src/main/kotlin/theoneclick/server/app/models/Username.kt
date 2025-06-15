package theoneclick.server.app.models

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.dtos.UsernameDto

@JvmInline
@Serializable
value class Username private constructor(val value: String) {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    companion object Companion {
        private const val ERROR_MESSAGE = "Invalid username"

        private val REGEX = "^[a-zA-Z0-9_]{3,20}$".toRegex()

        fun isValid(value: String): Boolean = REGEX.matches(value)

        fun String.toUsername(): Username? =
            if (isValid(this)) Username(this) else null

        fun UsernameDto.toUsername(): Username = Username(value)
    }
}