package theoneclick.client.features.home.models

sealed interface HomesResult {
    data class Success(val homes: Homes?) : HomesResult
    data object Error : HomesResult
}