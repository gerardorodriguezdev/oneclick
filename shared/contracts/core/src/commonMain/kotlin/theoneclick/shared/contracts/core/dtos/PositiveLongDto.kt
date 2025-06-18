package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class PositiveLongDto private constructor(val value: Long) : Comparable<PositiveLongDto> {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    override fun compareTo(other: PositiveLongDto): Int = value.compareTo(other.value)

    companion object {
        private const val ERROR_MESSAGE = "Value must be non-negative"

        fun isValid(value: Long): Boolean = value >= 0

        fun Long.toPositiveLongDto(): PositiveLongDto? = if (isValid(this)) PositiveLongDto(this) else null

        fun unsafe(value: Long): PositiveLongDto = PositiveLongDto(value)
    }
}