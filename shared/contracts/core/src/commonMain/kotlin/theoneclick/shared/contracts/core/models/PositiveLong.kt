package theoneclick.shared.contracts.core.models

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class PositiveLong private constructor(val value: Long) : Comparable<PositiveLong> {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    override fun compareTo(other: PositiveLong): Int = value.compareTo(other.value)

    companion object Companion {
        private const val ERROR_MESSAGE = "Value must be non-negative"

        fun isValid(value: Long): Boolean = value >= 0

        fun Long.toPositiveLongDto(): PositiveLong? = if (isValid(this)) PositiveLong(this) else null

        fun unsafe(value: Long): PositiveLong = PositiveLong(value)
    }
}