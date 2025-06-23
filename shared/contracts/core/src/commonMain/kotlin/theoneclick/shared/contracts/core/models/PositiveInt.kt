package theoneclick.shared.contracts.core.models

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class PositiveInt private constructor(val value: Int) : Comparable<PositiveInt> {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    override fun compareTo(other: PositiveInt): Int = value.compareTo(other.value)

    companion object Companion {
        private const val ERROR_MESSAGE = "Value must be bigger than 0"

        fun isValid(value: Int): Boolean = value > 0

        fun Int.toPositiveIntDto(): PositiveInt? = if (isValid(this)) PositiveInt(this) else null

        fun unsafe(value: Int): PositiveInt = PositiveInt(value)
    }
}