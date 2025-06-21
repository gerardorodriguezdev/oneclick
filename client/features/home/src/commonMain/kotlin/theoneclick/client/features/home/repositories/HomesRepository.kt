package theoneclick.client.features.home.repositories

import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject
import theoneclick.client.features.home.dataSources.HomesDataSource
import theoneclick.client.features.home.models.GenericResult
import theoneclick.client.features.home.repositories.HomesRepository.Companion.defaultPageSize
import theoneclick.shared.contracts.core.dtos.HomeDto
import theoneclick.shared.contracts.core.dtos.NonNegativeIntDto
import theoneclick.shared.contracts.core.dtos.PaginationResultDto
import theoneclick.shared.contracts.core.dtos.PositiveIntDto

internal interface HomesRepository {
    val pagination: SharedFlow<PaginationResultDto<List<HomeDto>>?>

    fun refreshHomes(): Flow<GenericResult<PaginationResultDto<List<HomeDto>>?>>
    fun homes(
        pageSize: PositiveIntDto,
        currentPageIndex: NonNegativeIntDto,
    ): Flow<GenericResult<PaginationResultDto<List<HomeDto>>?>>

    companion object {
        val defaultPageSize = PositiveIntDto.unsafe(10)
    }
}

@Inject
internal class MemoryHomesRepository(
    private val remoteHomesDataSource: HomesDataSource,
) : HomesRepository {
    private val mutablePagination = MutableStateFlow<PaginationResultDto<List<HomeDto>>?>(null)
    override val pagination: StateFlow<PaginationResultDto<List<HomeDto>>?> = mutablePagination

    override fun refreshHomes(): Flow<GenericResult<PaginationResultDto<List<HomeDto>>?>> {
        return remoteHomesDataSource
            .homes(
                pageSize = defaultPageSize,
                currentPageIndex = NonNegativeIntDto.zero,
            )
            .onEach { result ->
                // Only update if success
                if (result is GenericResult.Success) {
                    mutablePagination.emit(result.value)
                }
            }
    }

    override fun homes(
        pageSize: PositiveIntDto,
        currentPageIndex: NonNegativeIntDto,
    ): Flow<GenericResult<PaginationResultDto<List<HomeDto>>?>> {
        return remoteHomesDataSource
            .homes(pageSize = pageSize, currentPageIndex = currentPageIndex)
            .onEach { result ->
                // Only update if success
                if (result is GenericResult.Success) {
                    val newPagination = result.value

                    val currentPagination = mutablePagination.value
                    if (currentPagination == null) {
                        mutablePagination.emit(newPagination)
                        return@onEach
                    }

                    if (newPagination == null) return@onEach

                    mutablePagination.emit(
                        currentPagination.copy(
                            lastModified = newPagination.lastModified,
                            value = currentPagination.value + newPagination.value,
                            pageIndex = newPagination.pageIndex,
                            totalPages = newPagination.totalPages,
                        )
                    )
                }
            }
    }
}
