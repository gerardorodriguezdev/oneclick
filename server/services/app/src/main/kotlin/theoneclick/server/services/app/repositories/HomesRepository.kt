package theoneclick.server.services.app.repositories

import theoneclick.server.services.app.dataSources.base.HomesDataSource
import theoneclick.server.services.app.dataSources.models.HomesEntry
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.PaginationResult
import theoneclick.shared.contracts.core.models.PositiveInt
import theoneclick.shared.contracts.core.models.Uuid

interface HomesRepository {
    suspend fun homesEntry(
        userId: Uuid,
        pageSize: PositiveInt,
        currentPageIndex: NonNegativeInt,
    ): PaginationResult<HomesEntry>?
}

class DefaultHomesRepository(
    private val diskHomesDataSource: HomesDataSource,
    private val memoryHomesDataSource: HomesDataSource,
) : HomesRepository {

    override suspend fun homesEntry(
        userId: Uuid,
        pageSize: PositiveInt,
        currentPageIndex: NonNegativeInt,
    ): PaginationResult<HomesEntry>? {
        val memoryHomes = memoryHomesDataSource.homesEntry(userId, pageSize, currentPageIndex)
        if (memoryHomes != null) return memoryHomes

        val diskHomes = diskHomesDataSource.homesEntry(userId, pageSize, currentPageIndex)
        return diskHomes
    }
}
