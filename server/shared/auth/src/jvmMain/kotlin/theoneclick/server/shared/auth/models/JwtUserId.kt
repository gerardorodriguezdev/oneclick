package theoneclick.server.shared.auth.models

import dev.drewhamilton.poko.Poko
import kotlinx.serialization.Serializable

@Serializable
@Poko
class JwtUserId private constructor(val value: String) {
    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    companion object {
        private const val ERROR_MESSAGE = "Invalid jwt user id"

        private val REGEX = """^[A-Za-z0-9+/]{86}={0,2}$""".toRegex()

        private fun isValid(value: String): Boolean = REGEX.matches(value)

        fun String.toJwtUserId(): JwtUserId? = if (isValid(this)) JwtUserId(this) else null

        fun unsafe(value: String): JwtUserId = JwtUserId(value)
    }
}
