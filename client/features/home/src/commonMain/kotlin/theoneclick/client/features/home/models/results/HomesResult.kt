package theoneclick.client.features.home.models.results

import theoneclick.shared.contracts.core.dtos.HomeDto

sealed interface HomesResult {
    data class Success(val homes: List<HomeDto>) : HomesResult
    data object Error : HomesResult
}