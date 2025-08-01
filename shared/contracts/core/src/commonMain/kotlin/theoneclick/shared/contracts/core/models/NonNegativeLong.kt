package theoneclick.shared.contracts.core.models

import dev.drewhamilton.poko.Poko
import kotlinx.serialization.Serializable

@Poko
@Serializable
class NonNegativeLong private constructor(val value: Long) : Comparable<NonNegativeLong> {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    override fun compareTo(other: NonNegativeLong): Int = value.compareTo(other.value)

    companion object {
        private const val ERROR_MESSAGE = "Value must be non-negative"

        val zero = NonNegativeLong(0)

        fun isValid(value: Long): Boolean = value >= 0

        fun Long.toNonNegativeLong(): NonNegativeLong? = if (isValid(this)) NonNegativeLong(this) else null

        fun unsafe(value: Long): NonNegativeLong = NonNegativeLong(value)
    }
}
