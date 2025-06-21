package theoneclick.shared.contracts.core.dtos.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.dtos.HomeDto
import theoneclick.shared.contracts.core.dtos.PaginationResultDto

@Serializable
data class HomesResponseDto(
    val paginationResultDto: PaginationResultDto<List<HomeDto>>?,
)
