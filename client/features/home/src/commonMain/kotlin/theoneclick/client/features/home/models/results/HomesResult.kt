package theoneclick.client.features.home.models.results

import theoneclick.shared.contracts.core.dtos.HomesDto

sealed interface HomesResult {
    data class Success(val homes: HomesDto?) : HomesResult
    data object Error : HomesResult
}