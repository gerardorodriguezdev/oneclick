package theoneclick.shared.contracts.core.models

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class Password private constructor(val value: String) {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    companion object {
        private const val ERROR_MESSAGE = "Invalid password"

        private val REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$".toRegex()

        fun isValid(value: String): Boolean = REGEX.matches(value)

        fun String.toPassword(): Password? =
            if (isValid(this)) Password(this) else null
    }
}