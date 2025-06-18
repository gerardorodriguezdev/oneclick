package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class UsernameDto private constructor(val value: String) : UserKeyDto {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    companion object {
        private const val ERROR_MESSAGE = "Invalid username"

        private val REGEX = "^[a-zA-Z0-9_]{3,20}$".toRegex()

        fun isValid(value: String): Boolean = REGEX.matches(value)

        fun String.toUsername(): UsernameDto? =
            if (isValid(this)) UsernameDto(this) else null
    }
}