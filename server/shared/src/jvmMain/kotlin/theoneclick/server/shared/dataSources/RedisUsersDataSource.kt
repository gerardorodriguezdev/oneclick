package theoneclick.server.shared.dataSources

import io.ktor.util.logging.*
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import theoneclick.server.shared.dataSources.base.UsersDataSource
import theoneclick.server.shared.models.User
import theoneclick.shared.contracts.core.models.Username
import theoneclick.shared.contracts.core.models.Uuid
import theoneclick.shared.dispatchers.platform.DispatchersProvider

@OptIn(ExperimentalLettuceCoroutinesApi::class)
class RedisUsersDataSource(
    private val syncCommands: RedisCoroutinesCommands<String, String>,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
) : UsersDataSource {

    override suspend fun user(findable: UsersDataSource.Findable): User? =
        try {
            withContext(dispatchersProvider.io()) {
                val userJson = syncCommands.getUser(findable) ?: return@withContext null
                Json.decodeFromString<User>(userJson)
            }
        } catch (e: SerializationException) {
            logger.error("Error decoding user", e)
            syncCommands.deleteUser(findable)
            null
        } catch (e: Exception) {
            logger.error("Error trying to find user", e)
            null
        }

    override suspend fun saveUser(user: User): Boolean =
        try {
            withContext(dispatchersProvider.io()) {
                val userJson = Json.encodeToString(user)
                syncCommands.setUser(user, userJson)
                true
            }
        } catch (e: Exception) {
            logger.error("Error trying to save user", e)
            false
        }

    private companion object {
        const val USER_BY_USER_ID_PREFIX = "user:userId:"
        const val USER_BY_USERNAME_PREFIX = "user:username:"

        fun Uuid.toKey(): String = USER_BY_USER_ID_PREFIX + value
        fun Username.toKey(): String = USER_BY_USERNAME_PREFIX + value

        fun UsersDataSource.Findable.toKey(): String =
            when (this) {
                is UsersDataSource.Findable.ByUserId -> userId.toKey()
                is UsersDataSource.Findable.ByUsername -> username.toKey()
            }

        suspend fun RedisCoroutinesCommands<String, String>.getUser(findable: UsersDataSource.Findable): String? =
            get(findable.toKey())

        suspend fun RedisCoroutinesCommands<String, String>.setUser(user: User, userJson: String) {
            set(user.userId.toKey(), userJson)
            set(user.username.toKey(), userJson)
        }

        suspend fun RedisCoroutinesCommands<String, String>.deleteUser(findable: UsersDataSource.Findable) {
            del(findable.toKey())
        }
    }
}
