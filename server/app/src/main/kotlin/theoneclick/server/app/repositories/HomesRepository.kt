package theoneclick.server.app.repositories

import theoneclick.server.app.dataSources.base.HomesDataSource
import theoneclick.server.app.models.dtos.HomesEntryDto
import theoneclick.shared.contracts.core.dtos.*

interface HomesRepository {
    fun homesEntry(
        userId: UuidDto,
        pageSize: PositiveIntDto,
        currentPageIndex: NonNegativeIntDto,
    ): PaginationResultDto<HomesEntryDto>?
}

class DefaultHomesRepository(
    private val diskHomesDataSource: HomesDataSource,
    private val memoryHomesDataSource: HomesDataSource,
) : HomesRepository {

    override fun homesEntry(
        userId: UuidDto,
        pageSize: PositiveIntDto,
        currentPageIndex: NonNegativeIntDto,
    ): PaginationResultDto<HomesEntryDto>? {
        val memoryHomes = memoryHomesDataSource.homesEntry(userId, pageSize, currentPageIndex)
        if (memoryHomes != null) return memoryHomes

        val diskHomes = diskHomesDataSource.homesEntry(userId, pageSize, currentPageIndex)
        return if (diskHomes != null) {
            diskHomes
        } else {
            null
        }
    }
}