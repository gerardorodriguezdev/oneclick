package theoneclick.client.features.home.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import theoneclick.client.features.home.dataSources.HomesDataSource
import theoneclick.client.features.home.models.HomesEntry
import theoneclick.client.features.home.models.HomesResult
import theoneclick.client.features.home.repositories.HomesRepository.Companion.defaultPageSize
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.PositiveInt
import theoneclick.shared.contracts.core.models.UniqueList
import theoneclick.shared.contracts.core.models.UniqueList.Companion.plus
import theoneclick.shared.contracts.core.models.requests.HomesRequest

internal interface HomesRepository {
    val homesEntry: SharedFlow<HomesEntry?>

    suspend fun refreshHomes(): HomesResult
    suspend fun requestMoreHomes(): HomesResult

    companion object {
        val defaultPageSize = PositiveInt.unsafe(10)
    }
}

@Inject
internal class DefaultHomesRepository(
    private val remoteHomesDataSource: HomesDataSource,
) : HomesRepository {
    private val mutableHomesEntry = MutableStateFlow<HomesEntry?>(null)
    override val homesEntry: StateFlow<HomesEntry?> = mutableHomesEntry

    override suspend fun refreshHomes(): HomesResult {
        val homesResult = remoteHomesDataSource.homes(
            request = HomesRequest(
                pageSize = defaultPageSize,
                pageIndex = NonNegativeInt.zero,
            )
        )

        if (homesResult is HomesResult.Success) {
            mutableHomesEntry.emit(homesResult.homesEntry)
        }

        return homesResult
    }

    override suspend fun requestMoreHomes(): HomesResult {
        val homesResult = remoteHomesDataSource
            .homes(
                request = HomesRequest(
                    pageSize = defaultPageSize,
                    pageIndex = mutableHomesEntry.value?.pageIndex ?: NonNegativeInt.zero,
                )
            )

        if (homesResult is HomesResult.Success && homesResult.homesEntry != null) {
            val currentHomes = mutableHomesEntry.value?.homes ?: UniqueList.emptyUniqueList()
            val newHomesEntry = homesResult.homesEntry
            mutableHomesEntry.emit(
                HomesEntry(
                    homes = currentHomes + newHomesEntry.homes,
                    pageIndex = newHomesEntry.pageIndex,
                    canRequestMore = newHomesEntry.canRequestMore,
                )
            )
        }

        return homesResult
    }
}
