package theoneclick.client.features.home.models.results

import theoneclick.shared.contracts.core.models.Home

internal sealed interface HomesResult {
    data class Success(val homes: List<Home>) : HomesResult
    data object Error : HomesResult
}
