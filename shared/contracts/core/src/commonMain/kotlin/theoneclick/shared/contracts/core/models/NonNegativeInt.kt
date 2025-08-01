package theoneclick.shared.contracts.core.models

import dev.drewhamilton.poko.Poko
import kotlinx.serialization.Serializable

@Poko
@Serializable
class NonNegativeInt private constructor(val value: Int) : Comparable<NonNegativeInt> {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    override fun compareTo(other: NonNegativeInt): Int = value.compareTo(other.value)

    companion object {
        private const val ERROR_MESSAGE = "Value must be non-negative"

        val zero = NonNegativeInt(0)

        fun isValid(value: Int): Boolean = value >= 0

        fun Int.toNonNegativeInt(): NonNegativeInt? = if (isValid(this)) NonNegativeInt(this) else null

        fun unsafe(value: Int): NonNegativeInt = NonNegativeInt(value)
    }
}
