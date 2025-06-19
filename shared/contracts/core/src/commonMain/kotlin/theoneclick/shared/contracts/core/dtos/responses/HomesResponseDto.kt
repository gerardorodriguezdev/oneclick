package theoneclick.shared.contracts.core.dtos.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.dtos.HomeDto
import theoneclick.shared.contracts.core.dtos.NonNegativeIntDto
import theoneclick.shared.contracts.core.dtos.PositiveLongDto

@Serializable
data class HomesResponseDto(
    val homesPagination: HomesPagination?,
) {
    @Serializable
    data class HomesPagination(
        val lastModified: PositiveLongDto,
        val homes: List<HomeDto>,
        val pageIndex: NonNegativeIntDto,
        val totalPages: NonNegativeIntDto,
    )
}
