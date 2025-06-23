package theoneclick.shared.contracts.core.models.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.Home
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.PositiveLong

@Serializable
data class HomesResponseDto(
    val data: DataDto?,
) {
    sealed interface DataDto {

        @Serializable
        data object NotChanged : DataDto

        @Serializable
        data class Success(
            val lastModified: PositiveLong,
            val value: List<Home>,
            val pageIndex: NonNegativeInt,
            val canRequestMore: Boolean,
        ) : DataDto
    }
}