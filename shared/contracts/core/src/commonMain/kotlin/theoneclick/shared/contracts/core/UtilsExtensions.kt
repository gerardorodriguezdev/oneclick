package theoneclick.shared.contracts.core

internal fun <T, K> List<T>.containsDuplicatesBy(block: (item: T) -> K): Boolean {
    val distinct = distinctBy(block)
    return size == distinct.size
}