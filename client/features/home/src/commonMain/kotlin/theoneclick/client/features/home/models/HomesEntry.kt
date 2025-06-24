package theoneclick.client.features.home.models

import theoneclick.shared.contracts.core.models.Home
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.PositiveLong
import theoneclick.shared.contracts.core.models.UniqueList

data class HomesEntry(
    val lastModified: PositiveLong,
    val homes: UniqueList<Home>,
    val pageIndex: NonNegativeInt,
    val canRequestMore: Boolean,
)