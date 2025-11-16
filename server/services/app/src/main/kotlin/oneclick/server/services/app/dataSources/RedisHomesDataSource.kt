package oneclick.server.services.app.dataSources

import io.ktor.util.logging.*
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import oneclick.server.services.app.dataSources.base.HomesDataSource
import oneclick.server.services.app.dataSources.models.HomesEntry
import oneclick.shared.contracts.core.models.*
import oneclick.shared.contracts.homes.models.Home
import oneclick.shared.dispatchers.platform.DispatchersProvider

@OptIn(ExperimentalLettuceCoroutinesApi::class)
internal class RedisHomesDataSource(
    private val syncCommands: RedisCoroutinesCommands<String, String>,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
) : HomesDataSource {

    override suspend fun homesEntry(
        userId: Uuid,
        pageSize: PositiveInt,
        currentPageIndex: NonNegativeInt
    ): PaginationResult<HomesEntry>? =
        withContext(dispatchersProvider.io()) {
            try {
                val currentPageIndex = currentPageIndex.value.toLong()

                val homes = syncCommands
                    .homes(userId, start = currentPageIndex, end = pageSize.value.toLong() + currentPageIndex)
                    .map { homeString ->
                        async { Json.decodeFromString<Home>(homeString) }
                    }
                    .awaitAll()

                PaginationResult(
                    value = HomesEntry(
                        userId = userId,
                        homes = UniqueList.unsafe(homes),
                    ),
                    pageIndex = NonNegativeInt.unsafe(currentPageIndex.toInt() + 1),
                    totalPages = NonNegativeInt.unsafe(syncCommands.totalHomes(userId)),
                )
            } catch (error: Exception) {
                logger.error("Error getting homes", error)
                null
            }
        }

    override suspend fun home(userId: Uuid, homeId: Uuid): Home? =
        withContext(dispatchersProvider.io()) {
            try {
                val homeString = syncCommands.home(userId = userId, homeId = homeId) ?: return@withContext null
                Json.decodeFromString<Home>(homeString)
            } catch (error: Exception) {
                logger.error("Error getting home", error)
                null
            }
        }

    override suspend fun saveHome(userId: Uuid, home: Home): Boolean =
        withContext(dispatchersProvider.io()) {
            try {
                syncCommands.saveHome(userId = userId, homeId = home.id, homeString = Json.encodeToString(home))
                true
            } catch (error: Exception) {
                logger.error("Error saving home", error)
                false
            }
        }

    private companion object {
        fun userHomeIdsByUserIdKey(userId: Uuid): String = "userHomes:userId:${userId.value}"
        fun homeByHomeIdKey(homeId: Uuid): String = "home:homeId:${homeId.value}"

        suspend fun RedisCoroutinesCommands<String, String>.saveHome(userId: Uuid, homeId: Uuid, homeString: String) {
            rpush(userHomeIdsByUserIdKey(userId), homeId.value)
            set(homeByHomeIdKey(homeId), homeString)
        }

        suspend fun RedisCoroutinesCommands<String, String>.home(userId: Uuid, homeId: Uuid): String? {
            val hasHome = lpos(userHomeIdsByUserIdKey(userId), homeId.value) ?: return null
            return if (hasHome >= 0) {
                get(homeByHomeIdKey(homeId))
            } else {
                null
            }
        }

        suspend fun RedisCoroutinesCommands<String, String>.homes(userId: Uuid, start: Long, end: Long): List<String> {
            val homeIds = lrange(userHomeIdsByUserIdKey(userId), start, end)
            return homeIds.mapNotNull { homeId ->
                get(homeByHomeIdKey(Uuid.unsafe(homeId)))
            }
        }

        suspend fun RedisCoroutinesCommands<String, String>.totalHomes(userId: Uuid): Int =
            llen(userHomeIdsByUserIdKey(userId))?.toInt() ?: 0
    }
}