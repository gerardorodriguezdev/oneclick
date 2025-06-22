package theoneclick.shared.contracts.core.dtos.requests

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.dtos.NonNegativeIntDto
import theoneclick.shared.contracts.core.dtos.PositiveIntDto
import theoneclick.shared.contracts.core.dtos.PositiveLongDto

@Serializable
data class HomesRequestDto(
    val lastModified: PositiveLongDto?,
    val pageSize: PositiveIntDto,
    val pageIndex: NonNegativeIntDto,
)