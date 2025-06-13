package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PositiveIntDto(val value: Int) : Comparable<PositiveIntDto> {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    override fun compareTo(other: PositiveIntDto): Int = value.compareTo(other.value)

    companion object {
        private const val ERROR_MESSAGE = "Value must be non-negative"

        fun isValid(value: Int): Boolean = value >= 0

        fun Int.toPositiveIntDto(): PositiveIntDto? = if (isValid(this)) PositiveIntDto(this) else null
    }
}