package theoneclick.client.features.home.models.results

import theoneclick.shared.contracts.core.dtos.HomeDto

internal sealed interface HomesResult {
    data class Success(val homeDtos: List<HomeDto>) : HomesResult
    data object Error : HomesResult
}
