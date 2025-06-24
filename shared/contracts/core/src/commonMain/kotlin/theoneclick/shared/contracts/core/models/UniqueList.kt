package theoneclick.shared.contracts.core.models

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.UniqueList.KeyProvider

@Serializable
class UniqueList<T : KeyProvider> private constructor(val elements: List<T>) : List<T> by elements {

    init {
        require(isValid(elements)) { ERROR_MESSAGE }
    }

    interface KeyProvider {
        val key: String
    }

    companion object {
        private const val ERROR_MESSAGE = "Duplicated elements"

        fun <T : KeyProvider> isValid(elements: List<T>): Boolean = !elements.containsDuplicates()

        fun <T : KeyProvider> List<T>.toUniqueList(): UniqueList<T>? =
            if (isValid(this)) UniqueList(this) else null

        fun <T : KeyProvider> emptyUniqueList(): UniqueList<T> = UniqueList(emptyList())

        fun <T : KeyProvider> unsafe(elements: List<T>): UniqueList<T> = UniqueList(elements)

        operator fun <T : KeyProvider> UniqueList<T>.plus(other: UniqueList<T>): UniqueList<T> =
            UniqueList(this.elements + other.elements)

        private fun <T : KeyProvider> List<T>.containsDuplicates(): Boolean {
            val distinct = distinctBy { it.key }
            return size == distinct.size
        }
    }
}