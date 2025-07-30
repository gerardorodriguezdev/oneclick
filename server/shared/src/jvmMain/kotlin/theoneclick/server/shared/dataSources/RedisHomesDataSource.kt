package theoneclick.server.shared.dataSources

import io.ktor.util.logging.*
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import theoneclick.server.shared.dataSources.base.HomesDataSource
import theoneclick.server.shared.models.HomesEntry
import theoneclick.shared.contracts.core.models.*
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import kotlin.coroutines.coroutineContext

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
            val parentContext = coroutineContext
            withContext(dispatchersProvider.io()) {
                val homes = homes(userId, pageSize, currentPageIndex)
                val paginationResult = PaginationResult(
                    value = HomesEntry(
                        userId = userId,
                        homes = homes,
                    ),
                    pageIndex = NonNegativeInt.unsafe(currentPageIndex.value + homes.size),
                    totalPages = syncCommands.totalHomes(userId),
                )
                withContext(parentContext) {
                    paginationResult
                }
            }
        } catch (e: SerializationException) {
            logger.error("Error decoding homes", e)
            syncCommands.deleteHomes(userId)
            null
        } catch (e: Exception) {
            logger.error("Error trying to find homes", e)
            null
        }

    private suspend fun homes(userId: Uuid, pageSize: PositiveInt, currentPageIndex: NonNegativeInt): UniqueList<Home> {
        val homesStrings = syncCommands.getHomes(userId, pageSize, currentPageIndex)
        val homesValues = homesStrings.map { homeValue -> Json.decodeFromString<HomeValue>(homeValue) }
        val homes = homesValues.map { (homeId, homeName) ->
            val rooms = rooms(homeId)
            Home(
                id = Uuid.unsafe(homeId),
                name = HomeName.unsafe(homeName),
                rooms = rooms,
            )
        }
        return UniqueList.unsafe(homes)
    }

    private suspend fun rooms(homeId: String): UniqueList<Room> {
        val roomsStrings = syncCommands.getRooms(homeId)
        val roomsValues = roomsStrings.map { roomValue -> Json.decodeFromString<RoomValue>(roomValue) }
        val rooms = roomsValues.map { (roomId, roomName) ->
            val devices = devices(roomId)
            Room(
                id = Uuid.unsafe(roomId),
                name = RoomName.unsafe(roomName),
                devices = devices,
            )
        }
        return UniqueList.unsafe(rooms)
    }

    private suspend fun devices(roomId: String): UniqueList<Device> {
        val devicesStrings = syncCommands.getDevices(roomId)
        val devicesValues = devicesStrings.map { deviceValue -> Json.decodeFromString<DeviceValue>(deviceValue) }
        val devices = devicesValues.map { (deviceString) -> Json.decodeFromString<Device>(deviceString) }
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

    @Serializable
    private data class DeviceValue(
        val deviceString: String
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

        suspend fun RedisCoroutinesCommands<String, String>.getDevices(roomId: String): List<String> =
            smembers(deviceKey(roomId)).toList()
    }
}
