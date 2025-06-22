package theoneclick.server.app.dataSources

import theoneclick.server.app.dataSources.base.HomesDataSource
import theoneclick.server.app.models.dtos.HomesEntryDto
import theoneclick.shared.contracts.core.dtos.NonNegativeIntDto
import theoneclick.shared.contracts.core.dtos.PaginationResultDto
import theoneclick.shared.contracts.core.dtos.PositiveIntDto
import theoneclick.shared.contracts.core.dtos.UuidDto

class MemoryHomesDataSource : HomesDataSource() {
    private val homesEntries = linkedMapOf<UuidDto, HomesEntryDto>()

    override fun homesEntry(
        userId: UuidDto,
        pageSize: PositiveIntDto,
        currentPageIndex: NonNegativeIntDto
    ): PaginationResultDto<HomesEntryDto>? {
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