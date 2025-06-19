package theoneclick.server.app.models.dtos

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.dtos.HomeDto
import theoneclick.shared.contracts.core.dtos.PositiveLongDto
import theoneclick.shared.contracts.core.dtos.UuidDto

@Serializable
data class HomesEntryDto(
    val userId: UuidDto,
    val lastModified: PositiveLongDto,
    val homes: List<HomeDto>,
)