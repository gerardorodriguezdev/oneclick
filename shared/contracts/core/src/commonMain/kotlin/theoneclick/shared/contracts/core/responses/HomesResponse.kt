package theoneclick.shared.contracts.core.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.Home

@Serializable
data class HomesResponse(
    val homes: List<Home>,
)
