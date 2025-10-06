package oneclick.server.services.app.dataSources.base

import oneclick.server.services.app.dataSources.models.HomesEntry
import oneclick.shared.contracts.core.models.NonNegativeInt
import oneclick.shared.contracts.core.models.PaginationResult
import oneclick.shared.contracts.core.models.PositiveInt
import oneclick.shared.contracts.core.models.Uuid

internal interface HomesDataSource {
    suspend fun homesEntry(
        userId: Uuid,
        pageSize: PositiveInt,
        currentPageIndex: NonNegativeInt
    ): PaginationResult<HomesEntry>?
}
