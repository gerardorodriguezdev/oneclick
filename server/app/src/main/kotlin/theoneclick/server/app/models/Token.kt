package theoneclick.server.app.models

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Token private constructor(val value: String) {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    companion object Companion {
        private const val ERROR_MESSAGE = "Invalid token"

        private val REGEX = "^[A-Za-z0-9-._~+/]+=*$".toRegex()

        private fun isValid(value: String): Boolean = REGEX.matches(value)

        fun String.toToken(): Token? =
            if (isValid(this)) Token(this) else null

        fun EncryptedToken.toToken(): Token = Token(token)

        fun unsafe(value: String): Token = Token(value)
    }
}