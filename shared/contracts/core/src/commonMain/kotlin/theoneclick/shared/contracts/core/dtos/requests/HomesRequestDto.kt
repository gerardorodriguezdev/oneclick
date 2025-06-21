package theoneclick.shared.contracts.core.dtos.requests

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.dtos.NonNegativeIntDto
import theoneclick.shared.contracts.core.dtos.PositiveIntDto

@Serializable
data class HomesRequestDto(
    val pageSize: PositiveIntDto,
    val pageIndex: NonNegativeIntDto,
)