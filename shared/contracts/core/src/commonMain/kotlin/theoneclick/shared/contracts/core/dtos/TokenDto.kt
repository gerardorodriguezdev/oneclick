package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
data class TokenDto(val value: String) {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    companion object Companion {
        private const val ERROR_MESSAGE = "Invalid token"

        private val REGEX = "^[A-Za-z0-9-._~+/]+=*$".toRegex()

        private fun isValid(value: String): Boolean = REGEX.matches(value)

        fun String.toToken(): TokenDto? =
            if (isValid(this)) TokenDto(this) else null
    }
}