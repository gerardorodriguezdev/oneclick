package theoneclick.client.features.home.models

sealed interface HomesResult {
    data class Success(val homesEntry: HomesEntry?) : HomesResult
    data object Error : HomesResult
}