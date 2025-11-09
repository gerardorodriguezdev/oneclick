package oneclick.server.services.app.dataSources

import io.ktor.util.logging.*
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import oneclick.server.services.app.dataSources.base.UsersDataSource
import oneclick.server.services.app.dataSources.base.UsersDataSource.Findable
import oneclick.server.services.app.dataSources.models.User
import oneclick.shared.contracts.auth.models.Username
import oneclick.shared.contracts.core.models.Uuid
import oneclick.shared.dispatchers.platform.DispatchersProvider

@OptIn(ExperimentalLettuceCoroutinesApi::class)
internal class RedisUsersDataSource(
    private val syncCommands: RedisCoroutinesCommands<String, String>,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
) : UsersDataSource {

    override suspend fun user(findable: Findable): User? =
        try {
            withContext(dispatchersProvider.io()) {
                val userJson = syncCommands.getUser(findable) ?: return@withContext null
                Json.decodeFromString<User>(userJson)
            }
        } catch (error: SerializationException) {
            logger.error("Error decoding user", error)
            syncCommands.deleteUser(findable)
            null
        } catch (error: Exception) {
            logger.error("Error trying to find user", error)
            null
        }

    override suspend fun saveUser(user: User): Boolean =
        try {
            withContext(dispatchersProvider.io()) {
                val userJson = Json.encodeToString(user)
                syncCommands.setUser(user, userJson)
                true
            }
        } catch (error: Exception) {
            logger.error("Error trying to save user", error)
            false
        }

    private companion object {
        const val USER_BY_USER_ID_PREFIX = "user:userId:"
        const val USER_BY_USERNAME_PREFIX = "user:username:"

        fun userByUserId(userId: Uuid): String = USER_BY_USER_ID_PREFIX + userId.value
        fun userByUsername(username: Username): String = USER_BY_USERNAME_PREFIX + username.value

        fun Findable.toKey(): String =
            when (this) {
                is Findable.ByUserId -> userByUserId(userId)
                is Findable.ByUsername -> userByUsername(username)
            }

        suspend fun RedisCoroutinesCommands<String, String>.getUser(findable: Findable): String? =
            get(findable.toKey())

        suspend fun RedisCoroutinesCommands<String, String>.setUser(user: User, userJson: String) {
            set(userByUserId(user.userId), userJson)
            set(userByUsername(user.username), userJson)
        }

        suspend fun RedisCoroutinesCommands<String, String>.deleteUser(findable: Findable) {
            del(findable.toKey())
        }
    }
}
