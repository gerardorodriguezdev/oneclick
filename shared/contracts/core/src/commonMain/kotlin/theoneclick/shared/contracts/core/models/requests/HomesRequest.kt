package theoneclick.shared.contracts.core.models.requests

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.PositiveInt

@Serializable
data class HomesRequest(
    val pageSize: PositiveInt,
    val pageIndex: NonNegativeInt,
)