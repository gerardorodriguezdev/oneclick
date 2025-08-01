package theoneclick.shared.contracts.core.models

import dev.drewhamilton.poko.Poko
import kotlinx.serialization.Serializable

@Poko
@Serializable
class RoomName private constructor(val value: String) {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    companion object {
        private const val ERROR_MESSAGE = "Invalid room name"

        private val REGEX = "^[a-zA-Z0-9_]{3,20}$".toRegex()

        private fun isValid(value: String): Boolean = REGEX.matches(value)

        fun String.toRoomName(): RoomName? =
            if (isValid(this)) RoomName(this) else null

        fun unsafe(value: String): RoomName = RoomName(value = value)
    }
}
