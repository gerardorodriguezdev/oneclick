package theoneclick.client.features.home.repositories

import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject
import theoneclick.client.features.home.dataSources.HomesDataSource
import theoneclick.client.features.home.models.HomesEntry
import theoneclick.client.features.home.models.HomesResult
import theoneclick.client.features.home.repositories.HomesRepository.Companion.defaultPageSize
import theoneclick.shared.contracts.core.dtos.NonNegativeIntDto
import theoneclick.shared.contracts.core.dtos.PositiveIntDto
import theoneclick.shared.contracts.core.dtos.PositiveLongDto
import theoneclick.shared.contracts.core.dtos.requests.HomesRequestDto

internal interface HomesRepository {
    val homesEntry: SharedFlow<HomesEntry?>

    fun refreshHomes(): Flow<HomesResult>
    fun requestMoreHomes(): Flow<HomesResult>

    companion object {
        val defaultPageSize = PositiveIntDto.unsafe(10)
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
                request = HomesRequestDto(
                    lastModified = mutableHomesEntry.value?.lastModified?.let {
                        PositiveLongDto.unsafe(it)
                    },
                    pageSize = defaultPageSize,
                    pageIndex = NonNegativeIntDto.zero,
                )
            )
            .onEach { homesResult ->
                if (homesResult is HomesResult.Success) {
                    mutableHomesEntry.emit(homesResult.homesEntry)
                }
            }

    override fun requestMoreHomes(): Flow<HomesResult> {
        val currentHomesEntry = mutableHomesEntry.value
        if (currentHomesEntry != null && !currentHomesEntry.canRequestMore) {
            return flow { emit(HomesResult.Success(homesEntry = null)) }
        }

        val pageIndex = currentHomesEntry?.pageIndex

        return remoteHomesDataSource
            .homes(
                request = HomesRequestDto(
                    lastModified = mutableHomesEntry.value?.lastModified?.let {
                        PositiveLongDto.unsafe(it)
                    },
                    pageSize = defaultPageSize,
                    pageIndex = if (pageIndex != null) {
                        NonNegativeIntDto.unsafe(pageIndex)
                    } else {
                        NonNegativeIntDto.zero
                    }
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
}
