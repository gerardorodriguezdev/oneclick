package theoneclick.shared.contracts.core.models

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class Username private constructor(val value: String) {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    companion object {
        private const val ERROR_MESSAGE = "Invalid username"

        private val REGEX = "^[a-zA-Z0-9_]{3,20}$".toRegex()

        fun isValid(value: String): Boolean = REGEX.matches(value)

        fun String.toUsername(): Username? =
            if (isValid(this)) Username(this) else null
    }
}