package oneclick.server.services.app.repositories

import oneclick.server.services.app.dataSources.base.HomesDataSource
import oneclick.server.services.app.dataSources.models.HomesEntry
import oneclick.shared.contracts.core.models.NonNegativeInt
import oneclick.shared.contracts.core.models.PaginationResult
import oneclick.shared.contracts.core.models.PositiveInt
import oneclick.shared.contracts.core.models.Uuid
import oneclick.shared.contracts.homes.models.Home

internal interface HomesRepository {
    suspend fun homesEntry(
        userId: Uuid,
        pageSize: PositiveInt,
        currentPageIndex: NonNegativeInt,
    ): PaginationResult<HomesEntry>?

    suspend fun home(userId: Uuid, homeId: Uuid): Home?

    suspend fun saveHome(userId: Uuid, home: Home): Boolean
}

internal class DefaultHomesRepository(
    private val memoryHomesDataSource: HomesDataSource,
    private val diskHomesDataSource: HomesDataSource,
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

    override suspend fun home(userId: Uuid, homeId: Uuid): Home? {
        val memoryHome = memoryHomesDataSource.home(userId = userId, homeId = homeId)
        if (memoryHome != null) return memoryHome

        val diskHome = diskHomesDataSource.home(userId = userId, homeId = homeId)
        return if (diskHome != null) {
            memoryHomesDataSource.saveHome(userId = userId, home = diskHome)
            diskHome
        } else {
            null
        }
    }

    override suspend fun saveHome(
        userId: Uuid,
        home: Home
    ): Boolean {
        memoryHomesDataSource.saveHome(userId = userId, home = home)
        return diskHomesDataSource.saveHome(userId = userId, home = home)
    }
}
