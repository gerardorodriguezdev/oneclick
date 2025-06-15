package theoneclick.shared.contracts.core.dtos.responses

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.dtos.HomeDto

@Serializable
data class HomesResponseDto(
    val homes: List<HomeDto>,
)
