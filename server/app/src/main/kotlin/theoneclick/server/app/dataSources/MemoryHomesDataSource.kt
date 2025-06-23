package theoneclick.server.app.dataSources

import theoneclick.server.app.dataSources.base.HomesDataSource
import theoneclick.server.app.models.HomesEntry
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.PaginationResult
import theoneclick.shared.contracts.core.models.PositiveInt
import theoneclick.shared.contracts.core.models.Uuid

class MemoryHomesDataSource : HomesDataSource() {
    private val homesEntries = linkedMapOf<Uuid, HomesEntry>()

    override fun homesEntry(
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

    private companion object {
        const val CLEAN_UP_LIMIT = 10_000
    }
}