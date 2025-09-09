package theoneclick.shared.contracts.homes.models

import dev.drewhamilton.poko.Poko
import kotlinx.serialization.Serializable

@Poko
@Serializable
class DeviceName private constructor(val value: String) {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    companion object {
        private const val ERROR_MESSAGE = "Invalid device name"

        private val REGEX = "^[a-zA-Z0-9_]{3,20}$".toRegex()

        private fun isValid(value: String): Boolean = REGEX.matches(value)

        fun String.toDeviceName(): DeviceName? =
            if (isValid(this)) DeviceName(this) else null

        fun unsafe(value: String): DeviceName = DeviceName(value)
    }
}
