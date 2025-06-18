package theoneclick.shared.contracts.core.dtos.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.dtos.HomesDto

@Serializable
data class HomesResponseDto(
    val homes: HomesDto?,
)
