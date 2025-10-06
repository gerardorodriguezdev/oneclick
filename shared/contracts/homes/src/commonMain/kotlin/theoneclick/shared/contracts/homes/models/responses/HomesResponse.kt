package oneclick.shared.contracts.homes.models.responses

import kotlinx.serialization.Serializable
import oneclick.shared.contracts.core.models.NonNegativeInt
import oneclick.shared.contracts.core.models.UniqueList
import oneclick.shared.contracts.homes.models.Home

@Serializable
data class HomesResponse(
    val data: Data?,
) {
    @Serializable
    data class Data(
        val homes: UniqueList<Home>,
        val pageIndex: NonNegativeInt,
        val canRequestMore: Boolean,
    )
}
