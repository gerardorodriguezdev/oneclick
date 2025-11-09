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
        val memoryHomes = memoryHomesDataSource.homesEntry(
            userId = userId,
            pageSize = pageSize,
            currentPageIndex = currentPageIndex
        )
        if (memoryHomes != null) return memoryHomes

        val diskHomes = diskHomesDataSource.homesEntry(
            userId = userId,
            pageSize = pageSize,
            currentPageIndex = currentPageIndex
        )
        return diskHomes
    }

    override suspend fun home(userId: Uuid, homeId: Uuid): Home? {
        val hasHome = hasHome(userId = userId, homeId = homeId)
        if (!hasHome) return null

        val memoryHome = memoryHomesDataSource.home(homeId = homeId)
        if (memoryHome != null) return memoryHome

        val diskHome = diskHomesDataSource.home(homeId = homeId)
        return if (diskHome != null) {
            memoryHomesDataSource.saveHome(userId = userId, home = diskHome)
            diskHome
        } else {
            null
        }
    }

    private suspend fun hasHome(
        userId: Uuid,
        homeId: Uuid
    ): Boolean {
        val hasHome = memoryHomesDataSource.hasHome(userId = userId, homeId = homeId)

        return if (hasHome) {
            true
        } else {
            diskHomesDataSource.hasHome(userId = userId, homeId = homeId)
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
