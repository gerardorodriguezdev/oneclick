package oneclick.server.services.app.dataSources

import io.ktor.util.logging.*
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import kotlinx.coroutines.withContext
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
                syncCommands.getUser(findable)
            }
        } catch (error: Exception) {
            logger.error("Error trying to find user", error)
            null
        }

    override suspend fun saveUser(user: User): Boolean =
        try {
            withContext(dispatchersProvider.io()) {
                val userString = Json.encodeToString(user)
                syncCommands.setUser(user, userString)
                true
            }
        } catch (error: Exception) {
            logger.error("Error trying to save user", error)
            false
        }

    private companion object {
        fun userByUserIdKey(userId: Uuid): String = "user:userId:${userId.value}"
        fun userByUsernameKey(username: Username): String = "user:username:${username.value}"

        fun Findable.toKey(): String =
            when (this) {
                is Findable.ByUserId -> userByUserIdKey(userId)
                is Findable.ByUsername -> userByUsernameKey(username)
            }

        suspend fun RedisCoroutinesCommands<String, String>.getUser(findable: Findable): User? {
            val userStringWithVersion = get(findable.toKey()) ?: return null

            val version = userStringWithVersion.substringBefore(':')
            val userString = userStringWithVersion.substringAfter(':')

            return when (version) {
                User.VERSION -> Json.decodeFromString<User>(userString)
                else -> null
            }
        }

        suspend fun RedisCoroutinesCommands<String, String>.setUser(user: User, userString: String) {
            val userStringWithVersion = "${User.VERSION}:$userString"
            set(userByUserIdKey(user.userId), userStringWithVersion)
            set(userByUsernameKey(user.username), userStringWithVersion)
        }
    }
}
