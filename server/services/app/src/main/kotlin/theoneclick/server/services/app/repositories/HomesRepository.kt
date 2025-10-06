package oneclick.server.services.app.repositories

import oneclick.server.services.app.dataSources.base.HomesDataSource
import oneclick.server.services.app.dataSources.models.HomesEntry
import oneclick.shared.contracts.core.models.NonNegativeInt
import oneclick.shared.contracts.core.models.PaginationResult
import oneclick.shared.contracts.core.models.PositiveInt
import oneclick.shared.contracts.core.models.Uuid

internal interface HomesRepository {
    suspend fun homesEntry(
        userId: Uuid,
        pageSize: PositiveInt,
        currentPageIndex: NonNegativeInt,
    ): PaginationResult<HomesEntry>?
}

internal class DefaultHomesRepository(
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
