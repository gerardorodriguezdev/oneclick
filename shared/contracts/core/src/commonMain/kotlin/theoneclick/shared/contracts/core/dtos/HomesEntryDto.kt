package theoneclick.shared.contracts.core.dtos

import kotlinx.serialization.Serializable

@Serializable
data class HomesEntryDto(
    val userId: UuidDto,
    val lastModified: PositiveLongDto,
    val homes: List<HomeDto>,
)