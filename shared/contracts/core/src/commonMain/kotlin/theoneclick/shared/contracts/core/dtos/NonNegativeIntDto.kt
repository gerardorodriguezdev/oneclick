package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class NonNegativeIntDto private constructor(val value: Int) : Comparable<NonNegativeIntDto> {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    override fun compareTo(other: NonNegativeIntDto): Int = value.compareTo(other.value)

    companion object {
        private const val ERROR_MESSAGE = "Value must be non-negative"

        val zero = NonNegativeIntDto(0)

        fun isValid(value: Int): Boolean = value >= 0

        fun Int.toNonNegativeIntDto(): NonNegativeIntDto? = if (isValid(this)) NonNegativeIntDto(this) else null

        fun unsafe(value: Int): NonNegativeIntDto = NonNegativeIntDto(value)
    }
}