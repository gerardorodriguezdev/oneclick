package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
class PositiveIntRangeDto private constructor(
    val start: PositiveIntDto,
    val end: PositiveIntDto,
) {
    init {
        require(isValid(start, end)) { ERROR_MESSAGE }
    }

    fun inRange(value: PositiveIntDto): Boolean = value in start..end

    companion object {
        private const val ERROR_MESSAGE = "End was bigger than start"

        private fun isValid(start: PositiveIntDto, end: PositiveIntDto): Boolean = start <= end

        fun positiveIntRangeDto(start: PositiveIntDto, end: PositiveIntDto): PositiveIntRangeDto? =
            if (isValid(start = start, end = end)) {
                PositiveIntRangeDto(start = start, end = end)
            } else {
                null
            }

        fun unsafe(start: PositiveIntDto, end: PositiveIntDto): PositiveIntRangeDto =
            PositiveIntRangeDto(start = start, end = end)
    }
}