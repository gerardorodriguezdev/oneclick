package oneclick.server.services.app.dataSources.base

import oneclick.server.services.app.dataSources.models.HomesEntry
import oneclick.shared.contracts.core.models.NonNegativeInt
import oneclick.shared.contracts.core.models.PaginationResult
import oneclick.shared.contracts.core.models.PositiveInt
import oneclick.shared.contracts.core.models.Uuid
import oneclick.shared.contracts.homes.models.Home

internal interface HomesDataSource {
    suspend fun homesEntry(
        userId: Uuid,
        pageSize: PositiveInt,
        currentPageIndex: NonNegativeInt
    ): PaginationResult<HomesEntry>?

    suspend fun hasHome(userId: Uuid, homeId: Uuid): Boolean

    suspend fun home(homeId: Uuid): Home?

    suspend fun saveHome(userId: Uuid, home: Home): Boolean
}
