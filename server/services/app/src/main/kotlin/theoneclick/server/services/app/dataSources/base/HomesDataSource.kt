package theoneclick.server.services.app.dataSources.base

import theoneclick.server.services.app.dataSources.models.HomesEntry
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.PaginationResult
import theoneclick.shared.contracts.core.models.PositiveInt
import theoneclick.shared.contracts.core.models.Uuid

interface HomesDataSource {
    suspend fun homesEntry(
        userId: Uuid,
        pageSize: PositiveInt,
        currentPageIndex: NonNegativeInt
    ): PaginationResult<HomesEntry>?
}
