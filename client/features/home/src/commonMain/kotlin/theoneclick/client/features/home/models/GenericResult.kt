package theoneclick.client.features.home.models

sealed interface GenericResult<out T> {
    data class Success<T>(val value: T) : GenericResult<T>
    data object Error : GenericResult<Nothing>
}