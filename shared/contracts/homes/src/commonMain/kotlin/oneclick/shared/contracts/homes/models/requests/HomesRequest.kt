package oneclick.shared.contracts.homes.models.requests

import kotlinx.serialization.Serializable
import oneclick.shared.contracts.core.models.NonNegativeInt
import oneclick.shared.contracts.core.models.PositiveInt

@Serializable
data class HomesRequest(
    val pageSize: PositiveInt,
    val pageIndex: NonNegativeInt,
)
