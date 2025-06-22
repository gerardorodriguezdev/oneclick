package theoneclick.shared.contracts.core.dtos.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.dtos.HomeDto
import theoneclick.shared.contracts.core.dtos.NonNegativeIntDto
import theoneclick.shared.contracts.core.dtos.PositiveLongDto

@Serializable
data class HomesResponseDto(
    val data: Data?,
) {
    @Serializable
    data class Data(
        val lastModified: PositiveLongDto,
        val value: List<HomeDto>,
        val pageIndex: NonNegativeIntDto,
        val canRequestMore: Boolean,
    )
}
