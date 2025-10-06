package oneclick.client.features.home.models

import oneclick.shared.contracts.core.models.NonNegativeInt
import oneclick.shared.contracts.core.models.UniqueList
import oneclick.shared.contracts.homes.models.Home

data class HomesEntry(
    val homes: UniqueList<Home>,
    val pageIndex: NonNegativeInt,
    val canRequestMore: Boolean,
)
