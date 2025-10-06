package oneclick.shared.contracts.core.models

import dev.drewhamilton.poko.Poko
import kotlinx.serialization.Serializable

@Poko
@Serializable
class PositiveInt private constructor(val value: Int) : Comparable<PositiveInt> {

    init {
        require(isValid(value)) { ERROR_MESSAGE }
    }

    override fun compareTo(other: PositiveInt): Int = value.compareTo(other.value)

    companion object {
        private const val ERROR_MESSAGE = "Value must be bigger than 0"

        fun isValid(value: Int): Boolean = value > 0

        fun Int.toPositiveInt(): PositiveInt? = if (isValid(this)) PositiveInt(this) else null

        fun unsafe(value: Int): PositiveInt = PositiveInt(value)
    }
}
