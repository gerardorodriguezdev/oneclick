package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UsernameDto(val value: String) {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    companion object Companion {
        private const val ERROR_MESSAGE = "Invalid username"

        private val REGEX = "^[a-zA-Z0-9_]{3,20}$".toRegex()

        fun isValid(value: String?): Boolean = if (value == null) false else REGEX.matches(value)

        fun String.toUsername(): UsernameDto? =
            if (isValid(this)) UsernameDto(this) else null
    }
}