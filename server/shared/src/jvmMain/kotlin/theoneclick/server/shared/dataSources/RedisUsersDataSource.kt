package theoneclick.server.shared.dataSources

import io.ktor.util.logging.*
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import theoneclick.server.shared.dataSources.base.UsersDataSource
import theoneclick.server.shared.models.User
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import kotlin.coroutines.coroutineContext

@OptIn(ExperimentalLettuceCoroutinesApi::class)
class RedisUsersDataSource(
    private val syncCommands: RedisCoroutinesCommands<String, String>,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
) : UsersDataSource {

    // TODO: Safe syncCommands usage
    // TODO: Clear if decoded error
    override suspend fun user(findable: UsersDataSource.Findable): User? =
        try {
            val parentContext = coroutineContext
            withContext(dispatchersProvider.io()) {
                when (findable) {
                    is UsersDataSource.Findable.ByUserId -> {
                        val userJson = syncCommands.get(userIdKey(findable.userId.value))
                            ?: return@withContext null
                        val user = Json.decodeFromString<User>(userJson)
                        withContext(parentContext) {
                            user
                        }
                    }

                    is UsersDataSource.Findable.ByUsername -> {
                        val userJson = syncCommands.get(userIdKey(findable.username.value))
                            ?: return@withContext null
                        val user = Json.decodeFromString<User>(userJson)
                        withContext(parentContext) {
                            user
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Error trying to find user", e)
            null
        }

    override suspend fun saveUser(user: User): Boolean =
        try {
            val parentContext = coroutineContext
            withContext(dispatchersProvider.io()) {
                val userJson = Json.encodeToString(user)
                syncCommands.set(userIdKey(user.userId.value), userJson)
                syncCommands.set(usernameKey(user.username.value), userJson)
                withContext(parentContext) {
                    true
                }
            }
        } catch (e: Exception) {
            logger.error("Error trying to save user", e)
            false
        }

    private companion object {
        const val USER_BY_ID_PREFIX = "user:id:"
        const val USER_BY_USERNAME_PREFIX = "user:username:"

        fun userIdKey(userId: String): String = USER_BY_ID_PREFIX + userId
        fun usernameKey(userId: String): String = USER_BY_USERNAME_PREFIX + userId
    }
}
