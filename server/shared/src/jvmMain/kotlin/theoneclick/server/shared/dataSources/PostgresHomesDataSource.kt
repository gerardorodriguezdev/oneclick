package theoneclick.server.shared.dataSources

import io.ktor.util.logging.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import theoneclick.server.shared.dataSources.base.HomesDataSource
import theoneclick.server.shared.models.HomesEntry
import theoneclick.server.shared.postgresql.Devices
import theoneclick.server.shared.postgresql.Homes
import theoneclick.server.shared.postgresql.Rooms
import theoneclick.server.shared.postgresql.UsersDatabase
import theoneclick.shared.contracts.core.models.*
import theoneclick.shared.contracts.core.models.NonNegativeInt.Companion.toNonNegativeInt
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import kotlin.coroutines.coroutineContext

class PostgresHomesDataSource(
    private val database: UsersDatabase,
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
                val homes = database.homesQueries.homesByUserId(
                    user_id = userId.value,
                    value_ = pageSize.value.toLong(),
                    value__ = currentPageIndex.value.toLong()
                ).executeAsList()

                val paginationResult = PaginationResult(
                    value = HomesEntry(
                        userId = userId,
                        homes = homes.toHomes(),
                    ),
                    pageIndex = NonNegativeInt.unsafe(currentPageIndex.value + 1),
                    totalPages = totalHomes()
                )
                withContext(parentContext) {
                    paginationResult
                }
            }
        } catch (e: Exception) {
            logger.error("Error getting homes", e)
            null
        }

    private fun List<Homes>.toHomes(): UniqueList<Home> =
        UniqueList.unsafe(
            map { home ->
                val rooms = database.roomsQueries.roomsByHomeName(home.home_name).executeAsList()

                Home(
                    name = HomeName.unsafe(home.home_name),
                    rooms = rooms.toRooms()
                )
            }
        )

    private fun List<Rooms>.toRooms(): UniqueList<Room> =
        if (isEmpty()) {
            UniqueList.emptyUniqueList()
        } else {
            UniqueList.unsafe(
                map { room ->
                    val devices =
                        database.devicesQueries.deviceByRoomName(room.room_name)
                            .executeAsList()

                    Room(
                        name = RoomName.unsafe(room.room_name),
                        devices = devices.toDevices()
                    )
                }
            )
        }

    private fun List<Devices>.toDevices(): UniqueList<Device> =
        if (isEmpty()) {
            UniqueList.emptyUniqueList()
        } else {
            UniqueList.unsafe(
                map { device ->
                    Json.decodeFromString<Device>(device.device)
                }
            )
        }

    private fun totalHomes(): NonNegativeInt =
        database
            .homesQueries
            .totalHomes()
            .executeAsOneOrNull()
            ?.toInt()
            ?.toNonNegativeInt()
            ?: NonNegativeInt.zero
}
