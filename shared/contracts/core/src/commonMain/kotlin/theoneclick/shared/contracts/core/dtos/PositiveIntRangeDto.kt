package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PositiveIntRangeDto(
    val start: PositiveIntDto,
    val end: PositiveIntDto,
) {
    init {
        require(areValid(start, end)) { ERROR_MESSAGE }
    }

    fun inRange(value: PositiveIntDto): Boolean = value in start..end

    companion object {
        private const val ERROR_MESSAGE = "End was bigger than start"

        private fun areValid(start: PositiveIntDto, end: PositiveIntDto): Boolean = start <= end

        fun positiveIntRangeDto(start: PositiveIntDto, end: PositiveIntDto): PositiveIntRangeDto? =
            if (areValid(start, end)) PositiveIntRangeDto(start, end) else null
    }
}