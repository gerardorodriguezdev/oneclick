package theoneclick.client.features.home.models.results

import theoneclick.client.features.home.models.entities.Home

sealed interface HomesResult {
    data class Success(val homes: List<Home>) : HomesResult
    data object Error : HomesResult
}