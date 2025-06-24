package theoneclick.shared.contracts.core.models

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.UniqueList.KeyProvider

@Serializable
class UniqueList<T> private constructor(val elements: List<KeyProvider<T>>) : List<KeyProvider<T>> by elements {

    init {
        require(isValid(elements)) { ERROR_MESSAGE }
    }

    interface KeyProvider<T> {
        val key: T
    }

    companion object {
        private const val ERROR_MESSAGE = "Duplicated elements"

        fun <T> isValid(elements: List<KeyProvider<T>>): Boolean = !elements.containsDuplicates()

        fun <T> List<KeyProvider<T>>.toUniqueList(): UniqueList<T>? =
            if (isValid(this)) UniqueList(this) else null

        fun <T> unsafe(elements: List<KeyProvider<T>>): UniqueList<T> = UniqueList(elements)

        private fun <T> List<KeyProvider<T>>.containsDuplicates(): Boolean {
            val distinct = distinctBy { it.key }
            return size == distinct.size
        }
    }
}