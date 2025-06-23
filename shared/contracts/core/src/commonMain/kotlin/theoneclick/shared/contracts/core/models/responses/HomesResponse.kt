package theoneclick.shared.contracts.core.models.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.Home
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.PositiveLong

@Serializable
data class HomesResponse(
    val data: Data?,
) {
    sealed interface Data {

        @Serializable
        data object NotChanged : Data

        @Serializable
        data class Success(
            val lastModified: PositiveLong,
            val value: List<Home>,
            val pageIndex: NonNegativeInt,
            val canRequestMore: Boolean,
        ) : Data
    }
}