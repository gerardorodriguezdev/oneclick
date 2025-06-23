package theoneclick.shared.contracts.core.models

import kotlinx.serialization.Serializable

@Serializable
class PositiveIntRange private constructor(
    val start: NonNegativeInt,
    val end: NonNegativeInt,
) {
    init {
        require(isValid(start = start, end = end)) { ERROR_MESSAGE }
    }

    fun inRange(value: NonNegativeInt): Boolean = value in start..end

    companion object Companion {
        private const val ERROR_MESSAGE = "End was bigger than start"

        private fun isValid(start: NonNegativeInt, end: NonNegativeInt): Boolean = start <= end

        fun positiveIntRangeDto(start: NonNegativeInt, end: NonNegativeInt): PositiveIntRange? =
            if (isValid(start = start, end = end)) {
                PositiveIntRange(start = start, end = end)
            } else {
                null
            }

        fun unsafe(start: NonNegativeInt, end: NonNegativeInt): PositiveIntRange =
            PositiveIntRange(start = start, end = end)
    }
}