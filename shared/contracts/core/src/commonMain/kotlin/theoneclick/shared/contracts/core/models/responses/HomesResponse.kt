package theoneclick.shared.contracts.core.models.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.Home
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.UniqueList

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