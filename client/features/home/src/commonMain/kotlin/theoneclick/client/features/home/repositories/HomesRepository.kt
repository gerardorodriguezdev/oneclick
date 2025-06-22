package theoneclick.client.features.home.repositories

import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject
import theoneclick.client.features.home.dataSources.HomesDataSource
import theoneclick.client.features.home.models.Home
import theoneclick.client.features.home.models.HomesResult
import theoneclick.client.features.home.models.PaginationResult
import theoneclick.client.features.home.repositories.HomesRepository.Companion.defaultPageSize
import theoneclick.shared.contracts.core.dtos.NonNegativeIntDto
import theoneclick.shared.contracts.core.dtos.PositiveIntDto
import theoneclick.shared.contracts.core.dtos.requests.HomesRequestDto

internal interface HomesRepository {
    val pagination: SharedFlow<PaginationResult<List<Home>>?>

    fun refreshHomes(): Flow<HomesResult>
    fun homes(currentPageIndex: Int): Flow<HomesResult>

    companion object {
        val defaultPageSize = PositiveIntDto.unsafe(10)
    }
}

@Inject
internal class MemoryHomesRepository(
    private val remoteHomesDataSource: HomesDataSource,
) : HomesRepository {
    private val mutablePagination = MutableStateFlow<PaginationResult<List<Home>>?>(null)
    override val pagination: StateFlow<PaginationResult<List<Home>>?> = mutablePagination

    override fun refreshHomes(): Flow<HomesResult> {
        return remoteHomesDataSource
            .homes(
                HomesRequestDto(
                    pageSize = defaultPageSize,
                    pageIndex = NonNegativeIntDto.zero,
                )
            )
            .onEach { result ->
                if (result is HomesResult.Success) {
                    mutablePagination.emit(result.paginationResult)
                }
            }
    }

    override fun homes(currentPageIndex: Int): Flow<HomesResult> =
        remoteHomesDataSource
            .homes(
                HomesRequestDto(
                    pageSize = defaultPageSize,
                    pageIndex = NonNegativeIntDto.unsafe(currentPageIndex),
                )
            )
            .onEach { result ->
                if (result is HomesResult.Success && result.paginationResult != null) {
                    val currentPagination = mutablePagination.value
                    val newPagination = result.paginationResult
                    if (currentPagination == null) {
                        mutablePagination.emit(newPagination)
                    } else {
                        mutablePagination.emit(
                            currentPagination.copy(
                                lastModified = newPagination.lastModified,
                                value = currentPagination.value + newPagination.value,
                                pageIndex = newPagination.pageIndex,
                                canRequestMore = newPagination.canRequestMore,
                            )
                        )
                    }
                }
            }
}
