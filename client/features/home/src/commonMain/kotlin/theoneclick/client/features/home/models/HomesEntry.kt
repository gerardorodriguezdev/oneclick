package theoneclick.client.features.home.models

import theoneclick.client.features.home.mappers.toHomes
import theoneclick.shared.contracts.core.models.responses.HomesResponseDto

internal class HomesEntry private constructor(
    val lastModified: Long,
    val homes: List<Home>,
    val pageIndex: Int,
    val canRequestMore: Boolean,
) {
    fun prepend(previousHomes: List<Home>): HomesEntry =
        HomesEntry(
            lastModified = lastModified,
            homes = previousHomes + homes,
            pageIndex = pageIndex,
            canRequestMore = canRequestMore,
        )

    companion object {
        fun HomesResponseDto.DataDto.Success.toHomesEntry(): HomesEntry =
            HomesEntry(
                lastModified = lastModified.value,
                homes = value.toHomes(),
                pageIndex = pageIndex.value,
                canRequestMore = canRequestMore,
            )
    }
}