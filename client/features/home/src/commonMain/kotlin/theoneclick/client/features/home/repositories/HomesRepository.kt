package theoneclick.client.features.home.repositories

import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject
import theoneclick.client.features.home.dataSources.LoggedDataSource
import theoneclick.client.features.home.repositories.HomesRepository.HomesResult
import theoneclick.client.features.home.repositories.HomesRepository.HomesResult.Success.Home

internal interface HomesRepository {
    val homes: SharedFlow<List<Home>>

    fun refreshHomes(): Flow<HomesResult>

    sealed interface HomesResult {
        data class Success(val homes: List<Home>) : HomesResult {
            data class Home(
                val name: String,
                val rooms: List<Room>,
            ) {
                data class Room(
                    val name: String,
                    val devices: List<Device>,
                ) {
                    sealed interface Device {
                        val id: String
                        val name: String

                        data class WaterSensor(
                            override val id: String,
                            override val name: String,
                            val level: Int,
                        ) : Device
                    }
                }
            }
        }

        data object Error : HomesResult
    }
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
                if (result is HomesResult.Success) {
                    mutableHomes.emit(result.homes)
                }
            }
}
