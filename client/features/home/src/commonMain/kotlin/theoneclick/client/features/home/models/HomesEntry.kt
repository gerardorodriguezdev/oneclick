package theoneclick.client.features.home.models

import theoneclick.shared.contracts.core.models.Home
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.PositiveLong

data class HomesEntry(
    val lastModified: PositiveLong,
    val homes: List<Home>, //TODO: Replace for unique list
    val pageIndex: NonNegativeInt,
    val canRequestMore: Boolean,
)