package theoneclick.server.services.homes.dataSources

import io.ktor.util.logging.*
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import theoneclick.server.services.homes.dataSources.base.HomesDataSource
import theoneclick.server.shared.models.HomesEntry
import theoneclick.shared.contracts.core.models.*
import theoneclick.shared.dispatchers.platform.DispatchersProvider

@OptIn(ExperimentalLettuceCoroutinesApi::class)
class RedisHomesDataSource(
    private val syncCommands: RedisCoroutinesCommands<String, String>,
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
                val homes = homes(userId, pageSize, currentPageIndex)
                PaginationResult(
                    value = HomesEntry(
                        userId = userId,
                        homes = homes,
                    ),
                    pageIndex = NonNegativeInt.unsafe(currentPageIndex.value + homes.size),
                    totalPages = syncCommands.totalHomes(userId),
                )
            }
        } catch (error: SerializationException) {
            logger.error("Error decoding homes", error)
            syncCommands.deleteHomes(userId)
            null
        } catch (error: Exception) {
            logger.error("Error trying to find homes", error)
            null
        }

    private suspend fun CoroutineScope.homes(
        userId: Uuid,
        pageSize: PositiveInt,
        currentPageIndex: NonNegativeInt
    ): UniqueList<Home> {
        val homesStrings = syncCommands.getHomes(userId, pageSize, currentPageIndex)
        val homesValues = homesStrings.map { homeValue ->
            async { Json.decodeFromString<HomeValue>(homeValue) }
        }.awaitAll()
        val homes = homesValues.map { (homeId, homeName) ->
            async {
                val rooms = rooms(homeId)
                Home(
                    id = Uuid.unsafe(homeId),
                    name = HomeName.unsafe(homeName),
                    rooms = rooms,
                )
            }
        }.awaitAll()
        return UniqueList.unsafe(homes)
    }

    private suspend fun CoroutineScope.rooms(homeId: String): UniqueList<Room> {
        val roomsStrings = syncCommands.getRooms(homeId)
        val roomsValues = roomsStrings.map { roomValue ->
            async {
                try {
                    Json.decodeFromString<RoomValue>(roomValue)
                } finally {
                    syncCommands.deleteRooms(homeId)
                }
            }
        }.awaitAll()
        val rooms = roomsValues.map { (roomId, roomName) ->
            async {
                val devices = devices(roomId)
                Room(
                    id = Uuid.unsafe(roomId),
                    name = RoomName.unsafe(roomName),
                    devices = devices,
                )
            }
        }.awaitAll()
        return UniqueList.unsafe(rooms)
    }

    private suspend fun CoroutineScope.devices(roomId: String): UniqueList<Device> {
        val devicesStrings = syncCommands.getDevice(roomId)
        val devices = devicesStrings.map { deviceString ->
            async {
                try {
                    Json.decodeFromString<Device>(deviceString)
                } finally {
                    syncCommands.deleteDevice(roomId)
                }
            }
        }.awaitAll()
        return UniqueList.unsafe(devices)
    }

    @Serializable
    private data class HomeValue(
        val homeId: String,
        val homeName: String
    )

    @Serializable
    private data class RoomValue(
        val roomId: String,
        val roomName: String
    )

    private companion object {
        const val HOMES_BY_USER_ID_PREFIX = "home:userId:"
        const val ROOM_BY_HOME_ID = "room:homeId:"
        const val DEVICE_BY_ROOM_ID = "device:roomId:"

        fun homeKey(userId: Uuid) = HOMES_BY_USER_ID_PREFIX + userId.value
        fun roomKey(roomId: String) = ROOM_BY_HOME_ID + roomId
        fun deviceKey(deviceId: String) = DEVICE_BY_ROOM_ID + deviceId

        suspend fun RedisCoroutinesCommands<String, String>.totalHomes(userId: Uuid): NonNegativeInt =
            NonNegativeInt.unsafe(
                (zcard(homeKey(userId)) ?: 0).toInt()
            )

        suspend fun RedisCoroutinesCommands<String, String>.getHomes(
            userId: Uuid,
            pageSize: PositiveInt,
            currentPageIndex: NonNegativeInt,
        ): List<String> {
            val start = currentPageIndex.value.toLong()
            val stop = start + pageSize.value.toLong()
            return zrange(key = homeKey(userId), start = start, stop = stop).toList()
        }

        suspend fun RedisCoroutinesCommands<String, String>.deleteHomes(userId: Uuid) {
            del(homeKey(userId))
        }

        suspend fun RedisCoroutinesCommands<String, String>.getRooms(homeId: String): List<String> =
            smembers(roomKey(homeId)).toList()

        suspend fun RedisCoroutinesCommands<String, String>.deleteRooms(homeId: String) {
            del(roomKey(homeId))
        }

        suspend fun RedisCoroutinesCommands<String, String>.getDevice(roomId: String): List<String> =
            smembers(deviceKey(roomId)).toList()

        suspend fun RedisCoroutinesCommands<String, String>.deleteDevice(roomId: String) {
            del(deviceKey(roomId))
        }
    }
}
