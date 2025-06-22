package theoneclick.client.features.home.repositories

import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject
import theoneclick.client.features.home.dataSources.HomesDataSource
import theoneclick.client.features.home.models.HomesEntry
import theoneclick.client.features.home.models.HomesResult
import theoneclick.client.features.home.repositories.HomesRepository.Companion.defaultPageSize
import theoneclick.shared.contracts.core.dtos.NonNegativeIntDto
import theoneclick.shared.contracts.core.dtos.PositiveIntDto
import theoneclick.shared.contracts.core.dtos.requests.HomesRequestDto

internal interface HomesRepository {
    val homesEntry: SharedFlow<HomesEntry?>

    fun refreshHomes(): Flow<HomesResult>
    fun requestMoreHomes(currentPageIndex: Int): Flow<HomesResult>

    companion object {
        val defaultPageSize = PositiveIntDto.unsafe(10)
    }
}

@Inject
internal class MemoryHomesRepository(
    private val remoteHomesDataSource: HomesDataSource,
) : HomesRepository {
    private val mutableHomesEntry = MutableStateFlow<HomesEntry?>(null)
    override val homesEntry: StateFlow<HomesEntry?> = mutableHomesEntry

    override fun refreshHomes(): Flow<HomesResult> =
        remoteHomesDataSource
            .homes(
                HomesRequestDto(
                    pageSize = defaultPageSize,
                    pageIndex = NonNegativeIntDto.zero,
                )
            )
            .onEach { homesResult ->
                if (homesResult is HomesResult.Success) {
                    mutableHomesEntry.emit(homesResult.homesEntry)
                }
            }

    override fun requestMoreHomes(currentPageIndex: Int): Flow<HomesResult> =
        remoteHomesDataSource
            .homes(
                HomesRequestDto(
                    pageSize = defaultPageSize,
                    pageIndex = NonNegativeIntDto.unsafe(currentPageIndex),
                )
            )
            .onEach { homesResult ->
                if (homesResult is HomesResult.Success && homesResult.homesEntry != null) {
                    val currentHomes = mutableHomesEntry.value?.homes ?: emptyList()
                    val newHomesEntry = homesResult.homesEntry
                    mutableHomesEntry.emit(
                        HomesEntry(
                            lastModified = newHomesEntry.lastModified,
                            homes = currentHomes + newHomesEntry.homes,
                            pageIndex = newHomesEntry.pageIndex,
                            canRequestMore = newHomesEntry.canRequestMore,
                        )
                    )
                }
            }
}
