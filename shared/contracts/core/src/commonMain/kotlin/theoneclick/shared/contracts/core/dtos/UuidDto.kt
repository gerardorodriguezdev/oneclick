package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
class UuidDto private constructor(val value: String) {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    override fun equals(other: Any?): Boolean = other is UuidDto && other.value == value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value

    companion object {
        private const val ERROR_MESSAGE = "Invalid uuid"

        private val REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$".toRegex()

        private fun isValid(value: String): Boolean = REGEX.matches(value)

        fun String.toUuid(): UuidDto? =
            if (isValid(this)) UuidDto(this) else null

        fun unsafe(value: String): UuidDto = UuidDto(value)
    }
}