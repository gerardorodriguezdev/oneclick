package theoneclick.client.features.home.repositories

import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject
import theoneclick.client.features.home.dataSources.LoggedDataSource
import theoneclick.client.features.home.models.results.HomesResult
import theoneclick.shared.contracts.core.dtos.HomeDto

internal interface HomesRepository {
    val homes: SharedFlow<List<HomeDto>>

    fun refreshHomes(): Flow<HomesResult>
}

@Inject
internal class InMemoryHomesRepository(
    private val loggedDataSource: LoggedDataSource,
) : HomesRepository {
    private val mutableHomes = MutableStateFlow<List<HomeDto>>(emptyList())
    override val homes: StateFlow<List<HomeDto>> = mutableHomes

    override fun refreshHomes(): Flow<HomesResult> =
        loggedDataSource
            .homes()
            .onEach { result ->
                if (result is HomesResult.Success) {
                    mutableHomes.emit(result.homeDtos)
                }
            }
}
