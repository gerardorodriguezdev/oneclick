package theoneclick.shared.contracts.core.dtos.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.dtos.HomesEntryDto
import theoneclick.shared.contracts.core.dtos.PaginationResultDto

@Serializable
data class HomesResponseDto(
    val paginationResultDto: PaginationResultDto<HomesEntryDto>?,
)
