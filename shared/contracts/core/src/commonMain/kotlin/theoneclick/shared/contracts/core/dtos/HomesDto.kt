package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
data class HomesDto(
    val lastModified: PositiveLongDto,
    val homes: List<HomeDto>,
)