package theoneclick.server.app.dataSources.base

import theoneclick.server.app.models.dtos.HomesEntryDto
import theoneclick.shared.contracts.core.dtos.NonNegativeIntDto
import theoneclick.shared.contracts.core.dtos.PaginationResultDto
import theoneclick.shared.contracts.core.dtos.PositiveIntDto
import theoneclick.shared.contracts.core.dtos.UuidDto

abstract class HomesDataSource {

    abstract fun homesEntry(
        userId: UuidDto,
        pageSize: PositiveIntDto,
        currentPageIndex: NonNegativeIntDto
    ): PaginationResultDto<HomesEntryDto>?

    protected fun paginateHomesEntry(
        homesEntry: HomesEntryDto,
        pageSize: PositiveIntDto,
        currentPageIndex: NonNegativeIntDto
    ): PaginationResultDto<HomesEntryDto>? {
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

        val newHomesEntry = HomesEntryDto.unsafe(
            userId = homesEntry.userId,
            lastModified = homesEntry.lastModified,
            homes = newHomes,
        )

        return PaginationResultDto(
            value = newHomesEntry,
            pageIndex = NonNegativeIntDto.unsafe(newPageIndex),
            totalPages = NonNegativeIntDto.unsafe(homesEntry.homes.size),
        )
    }
}

