package theoneclick.server.app.dataSources.base

import theoneclick.server.shared.models.HomesEntry
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.PaginationResult
import theoneclick.shared.contracts.core.models.PositiveInt
import theoneclick.shared.contracts.core.models.UniqueList
import theoneclick.shared.contracts.core.models.Uuid

abstract class HomesDataSource {

    abstract fun homesEntry(
        userId: Uuid,
        pageSize: PositiveInt,
        currentPageIndex: NonNegativeInt
    ): PaginationResult<HomesEntry>?

    protected fun paginateHomesEntry(
        homesEntry: HomesEntry,
        pageSize: PositiveInt,
        currentPageIndex: NonNegativeInt
    ): PaginationResult<HomesEntry>? {
        val firstPageIndex = currentPageIndex.value + 1
        val lastPageIndex = firstPageIndex + pageSize.value

        var newPageIndex = 0
        val newHomes = buildList {
            for (index in firstPageIndex until lastPageIndex) {
                val home = homesEntry.homes.getOrNull(index)
                if (home != null) {
                    newPageIndex = index
                    add(home)
                } else {
                    break
                }
            }
        }
        if (newHomes.isEmpty()) return null

        val newHomesEntry = HomesEntry(
            userId = homesEntry.userId,
            lastModified = homesEntry.lastModified,
            homes = UniqueList.unsafe(newHomes),
        )

        return PaginationResult(
            value = newHomesEntry,
            pageIndex = NonNegativeInt.unsafe(newPageIndex),
            totalPages = NonNegativeInt.unsafe(homesEntry.homes.size),
        )
    }
}

