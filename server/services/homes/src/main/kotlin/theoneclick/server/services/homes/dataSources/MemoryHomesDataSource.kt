package theoneclick.server.services.homes.dataSources

import theoneclick.server.services.homes.dataSources.base.HomesDataSource
import theoneclick.server.shared.models.HomesEntry
import theoneclick.shared.contracts.core.models.*

class MemoryHomesDataSource(
    private val homesEntries: LinkedHashMap<Uuid, HomesEntry> = linkedMapOf(),
) : HomesDataSource {

    override suspend fun homesEntry(
        userId: Uuid,
        pageSize: PositiveInt,
        currentPageIndex: NonNegativeInt
    ): PaginationResult<HomesEntry>? {
        val homesEntry = homesEntries[userId] ?: return null

        return paginateHomesEntry(
            homesEntry = homesEntry,
            pageSize = pageSize,
            currentPageIndex = currentPageIndex
        )
    }

    private fun paginateHomesEntry(
        homesEntry: HomesEntry,
        pageSize: PositiveInt,
        currentPageIndex: NonNegativeInt
    ): PaginationResult<HomesEntry> {
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

        val newHomesEntry = HomesEntry(
            userId = homesEntry.userId,
            homes = UniqueList.unsafe(newHomes),
        )

        return PaginationResult(
            value = newHomesEntry,
            pageIndex = NonNegativeInt.unsafe(newPageIndex),
            totalPages = NonNegativeInt.unsafe(homesEntry.homes.size),
        )
    }
}
