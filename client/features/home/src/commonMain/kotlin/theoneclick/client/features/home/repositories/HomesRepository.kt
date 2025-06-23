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
                    lastModified = lastModifiedOrNull(),
                    pageSize = defaultPageSize,
                    pageIndex = NonNegativeIntDto.zero,
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
                request = HomesRequestDto(
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

    private fun pageIndex(): NonNegativeIntDto {
        val currentPageIndex = mutableHomesEntry.value?.pageIndex

        return if (currentPageIndex != null) {
            NonNegativeIntDto.unsafe(currentPageIndex)
        } else {
            NonNegativeIntDto.zero
        }
    }

    private fun lastModifiedOrNull(): PositiveLongDto? =
        mutableHomesEntry.value?.lastModified?.let {
            PositiveLongDto.unsafe(it)
        }
}
