package theoneclick.shared.contracts.core.models.requests

import kotlinx.serialization.Serializable
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.PositiveInt
import theoneclick.shared.contracts.core.models.PositiveLong

@Serializable
data class HomesRequestDto(
    val lastModified: PositiveLong?,
    val pageSize: PositiveInt,
    val pageIndex: NonNegativeInt,
)