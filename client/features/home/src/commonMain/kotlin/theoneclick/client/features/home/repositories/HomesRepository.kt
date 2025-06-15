package theoneclick.client.features.home.repositories

import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject
import theoneclick.client.features.home.dataSources.LoggedDataSource
import theoneclick.client.features.home.models.results.HomesResult
import theoneclick.client.features.home.models.results.HomesResult.Success.Home

internal interface HomesRepository {
    val homes: SharedFlow<List<Home>>

    fun refreshHomes(): Flow<HomesResult>
}

@Inject
internal class InMemoryHomesRepository(
    private val loggedDataSource: LoggedDataSource,
) : HomesRepository {
    private val mutableHomes = MutableStateFlow<List<Home>>(emptyList())
    override val homes: StateFlow<List<Home>> = mutableHomes

    override fun refreshHomes(): Flow<HomesResult> =
        loggedDataSource
            .homes()
            .onEach { result ->
                // Only update if success
                if (result is HomesResult.Success) {
                    mutableHomes.emit(result.homes)
                }
            }
}
