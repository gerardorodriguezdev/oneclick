package theoneclick.server.services.homes.dataSources

import io.ktor.util.logging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import theoneclick.server.services.homes.dataSources.base.HomesDataSource
import theoneclick.server.services.homes.postgresql.*
import theoneclick.server.services.homes.dataSources.models.HomesEntry
import theoneclick.shared.contracts.core.models.*
import theoneclick.shared.contracts.core.models.NonNegativeInt.Companion.toNonNegativeInt
import theoneclick.shared.dispatchers.platform.DispatchersProvider

class PostgresHomesDataSource(
    private val database: HomesDatabase,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
) : HomesDataSource {

    override suspend fun homesEntry(
        userId: Uuid,
        pageSize: PositiveInt,
        currentPageIndex: NonNegativeInt
    ): PaginationResult<HomesEntry>? =
        try {
            withContext(dispatchersProvider.io()) {
                val entries = database.homesQueries.homesByUserId(
                    user_id = userId.value,
                    value_ = pageSize.value.toLong(),
                    value__ = currentPageIndex.value.toLong()
                ).executeAsList()

                val (homes, rooms, devices) = entries.normalizeData()
                PaginationResult(
                    value = HomesEntry(
                        userId = userId,
                        homes = toHomes(homes, rooms, devices),
                    ),
                    pageIndex = NonNegativeInt.unsafe(currentPageIndex.value + 1),
                    totalPages = totalHomes()
                )
            }
        } catch (error: Exception) {
            logger.error("Error getting homes", error)
            null
        }

    private fun List<HomesByUserId>.normalizeData(): NormalizedData {
        val homes = hashSetOf<Homes>()
        val rooms = hashSetOf<Rooms>()
        val devices = hashSetOf<Devices>()

        forEach { entry ->
            homes.add(
                Homes(
                    user_id = entry.user_id,
                    home_id = entry.home_id,
                    home_name = entry.home_name
                )
            )

            rooms.add(
                Rooms(
                    home_id = entry.home_id,
                    room_id = entry.room_id,
                    room_name = entry.room_name,
                )
            )

            devices.add(
                Devices(
                    room_id = entry.room_id,
                    device_id = entry.device_id,
                    device = entry.device,
                )
            )
        }

        return NormalizedData(homes, rooms, devices)
    }

    private suspend fun CoroutineScope.toHomes(
        homes: HashSet<Homes>,
        rooms: HashSet<Rooms>,
        devices: HashSet<Devices>
    ): UniqueList<Home> =
        UniqueList.unsafe(
            homes.map { home ->
                async {
                    val rooms = rooms.filter { room -> room.home_id == home.home_id }
                    Home(
                        id = Uuid.unsafe(home.home_id),
                        name = HomeName.unsafe(home.home_name),
                        rooms = toRooms(rooms, devices)
                    )
                }
            }.awaitAll()
        )

    private suspend fun CoroutineScope.toRooms(rooms: List<Rooms>, devices: HashSet<Devices>): UniqueList<Room> =
        UniqueList.unsafe(
            rooms.map { room ->
                async {
                    val devices = devices.filter { device -> device.room_id == room.room_id }
                    Room(
                        id = Uuid.unsafe(room.room_id),
                        name = RoomName.unsafe(room.room_name),
                        devices = toDevices(devices)
                    )
                }
            }.awaitAll()
        )

    private suspend fun CoroutineScope.toDevices(devices: List<Devices>): UniqueList<Device> =
        UniqueList.unsafe(
            devices.map { device ->
                async {
                    try {
                        Json.decodeFromString<Device>(device.device)
                    } catch (error: SerializationException) {
                        logger.error("Error deserializing device", error)
                        database.devicesQueries.deleteByDeviceId(device.device_id)
                        null
                    }
                }
            }.awaitAll().filterNotNull()
        )

    private fun totalHomes(): NonNegativeInt =
        database
            .homesQueries
            .totalHomes()
            .executeAsOneOrNull()
            ?.toInt()
            ?.toNonNegativeInt()
            ?: NonNegativeInt.zero

    private data class NormalizedData(
        val homes: HashSet<Homes>,
        val rooms: HashSet<Rooms>,
        val devices: HashSet<Devices>,
    )
}
