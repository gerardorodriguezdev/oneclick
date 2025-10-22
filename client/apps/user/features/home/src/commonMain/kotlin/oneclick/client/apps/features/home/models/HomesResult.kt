package oneclick.client.apps.features.home.models

internal sealed interface HomesResult {
    data class Success(val homesEntry: HomesEntry?) : HomesResult
    data object Error : HomesResult
}
