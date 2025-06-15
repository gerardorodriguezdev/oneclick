package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class DeviceNameDto private constructor(val value: String) {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    companion object {
        private const val ERROR_MESSAGE = "Invalid device name"

        private val REGEX = "^[a-zA-Z0-9_]{3,20}$".toRegex()

        private fun isValid(value: String): Boolean = REGEX.matches(value)

        fun String.toDeviceName(): DeviceNameDto? =
            if (isValid(this)) DeviceNameDto(this) else null

        fun unsafe(value: String): DeviceNameDto = DeviceNameDto(value)
    }
}