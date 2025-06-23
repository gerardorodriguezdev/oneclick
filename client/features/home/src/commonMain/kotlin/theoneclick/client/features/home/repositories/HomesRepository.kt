package theoneclick.client.features.home.repositories

import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject
import theoneclick.client.features.home.dataSources.HomesDataSource
import theoneclick.client.features.home.models.HomesEntry
import theoneclick.client.features.home.models.HomesResult
import theoneclick.client.features.home.repositories.HomesRepository.Companion.defaultPageSize
import theoneclick.shared.contracts.core.models.NonNegativeInt
import theoneclick.shared.contracts.core.models.PositiveInt
import theoneclick.shared.contracts.core.models.PositiveLong
import theoneclick.shared.contracts.core.models.requests.HomesRequest

internal interface HomesRepository {
    val homesEntry: SharedFlow<HomesEntry?>

    fun refreshHomes(): Flow<HomesResult>
    fun requestMoreHomes(): Flow<HomesResult>

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

    override fun refreshHomes(): Flow<HomesResult> =
        remoteHomesDataSource
            .homes(
                request = HomesRequest(
                    lastModified = lastModifiedOrNull(),
                    pageSize = defaultPageSize,
                    pageIndex = NonNegativeInt.zero,
                )
            )
            .refreshCacheIfSuccess()

    private fun Flow<HomesResult>.refreshCacheIfSuccess(): Flow<HomesResult> =
        onEach { homesResult ->
            if (homesResult is HomesResult.Success) {
                mutableHomesEntry.emit(homesResult.homesEntry)
            }
        }

    override fun requestMoreHomes(): Flow<HomesResult> =
        remoteHomesDataSource
            .homes(
                request = HomesRequest(
                    lastModified = lastModifiedOrNull(),
                    pageSize = defaultPageSize,
                    pageIndex = pageIndex(),
                )
            )
            .appendToCacheIfAvailable()

    private fun Flow<HomesResult>.appendToCacheIfAvailable(): Flow<HomesResult> =
        onEach { homesResult ->
            if (homesResult is HomesResult.Success && homesResult.homesEntry != null) {
                val currentHomes = mutableHomesEntry.value?.homes ?: emptyList()
                val newHomesEntry = homesResult.homesEntry
                mutableHomesEntry.emit(
                    newHomesEntry.prepend(currentHomes)
                )
            }
        }

    private fun pageIndex(): NonNegativeInt {
        val currentPageIndex = mutableHomesEntry.value?.pageIndex

        return if (currentPageIndex != null) {
            NonNegativeInt.unsafe(currentPageIndex)
        } else {
            NonNegativeInt.zero
        }
    }

    private fun lastModifiedOrNull(): PositiveLong? =
        mutableHomesEntry.value?.lastModified?.let {
            PositiveLong.unsafe(it)
        }
}
