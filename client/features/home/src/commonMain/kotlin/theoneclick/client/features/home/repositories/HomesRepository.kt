package theoneclick.client.features.home.repositories

import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject
import theoneclick.client.features.home.dataSources.HomesDataSource
import theoneclick.client.features.home.models.Homes
import theoneclick.client.features.home.models.HomesResult
import theoneclick.client.features.home.repositories.HomesRepository.Companion.defaultPageSize
import theoneclick.shared.contracts.core.dtos.NonNegativeIntDto
import theoneclick.shared.contracts.core.dtos.PositiveIntDto
import theoneclick.shared.contracts.core.dtos.requests.HomesRequestDto

internal interface HomesRepository {
    val homes: SharedFlow<Homes?>

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
    private val mutableHomes = MutableStateFlow<Homes?>(null)
    override val homes: StateFlow<Homes?> = mutableHomes

    override fun refreshHomes(): Flow<HomesResult> {
        return remoteHomesDataSource
            .homes(
                HomesRequestDto(
                    pageSize = defaultPageSize,
                    pageIndex = NonNegativeIntDto.zero,
                )
            )
            .onEach { homesResult ->
                if (homesResult is HomesResult.Success) {
                    mutableHomes.emit(homesResult.homes)
                }
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
                if (homesResult is HomesResult.Success && homesResult.homes != null) {
                    val currentHomes = mutableHomes.value
                    val newHomes = homesResult.homes
                    if (currentHomes == null) {
                        mutableHomes.emit(newHomes)
                    } else {
                        mutableHomes.emit(
                            currentHomes.copy(
                                lastModified = newHomes.lastModified,
                                value = currentHomes.value + newHomes.value,
                                pageIndex = newHomes.pageIndex,
                                canRequestMore = newHomes.canRequestMore,
                            )
                        )
                    }
                }
            }
}
