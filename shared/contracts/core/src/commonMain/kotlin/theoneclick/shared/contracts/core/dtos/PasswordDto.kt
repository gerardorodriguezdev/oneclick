package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PasswordDto(val value: String) {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    companion object Companion {
        private const val ERROR_MESSAGE = "Invalid password"

        private val REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$".toRegex()

        fun isValid(value: String?): Boolean = if (value == null) false else REGEX.matches(value)

        fun String.toPassword(): PasswordDto? =
            if (isValid(this)) PasswordDto(this) else null
    }
}