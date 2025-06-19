package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
class PositiveIntRangeDto private constructor(
    val start: NonNegativeIntDto,
    val end: NonNegativeIntDto,
) {
    init {
        require(isValid(start = start, end = end)) { ERROR_MESSAGE }
    }

    fun inRange(value: NonNegativeIntDto): Boolean = value in start..end

    companion object {
        private const val ERROR_MESSAGE = "End was bigger than start"

        private fun isValid(start: NonNegativeIntDto, end: NonNegativeIntDto): Boolean = start <= end

        fun positiveIntRangeDto(start: NonNegativeIntDto, end: NonNegativeIntDto): PositiveIntRangeDto? =
            if (isValid(start = start, end = end)) {
                PositiveIntRangeDto(start = start, end = end)
            } else {
                null
            }

        fun unsafe(start: NonNegativeIntDto, end: NonNegativeIntDto): PositiveIntRangeDto =
            PositiveIntRangeDto(start = start, end = end)
    }
}