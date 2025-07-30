package theoneclick.server.shared.dataSources

import io.ktor.util.logging.*
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import theoneclick.server.shared.dataSources.base.SessionsDataSource
import theoneclick.server.shared.dataSources.base.SessionsDataSource.Findable
import theoneclick.server.shared.dataSources.base.SessionsDataSource.SessionEntry
import theoneclick.shared.contracts.core.models.Token
import theoneclick.shared.contracts.core.models.Uuid
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import kotlin.coroutines.coroutineContext

@OptIn(ExperimentalLettuceCoroutinesApi::class)
class RedisSessionsDataSource(
    private val syncCommands: RedisCoroutinesCommands<String, String>,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
) : SessionsDataSource {

    override suspend fun session(findable: Findable): SessionEntry? =
        try {
            val parentContext = coroutineContext
            withContext(dispatchersProvider.io()) {
                val sessionJson = syncCommands.getSession(findable) ?: return@withContext null
                val session = Json.decodeFromString<SessionEntry>(sessionJson)
                withContext(parentContext) {
                    session
                }
            }
        } catch (e: SerializationException) {
            logger.error("Error decoding session", e)
            syncCommands.deleteSession(findable)
            null
        } catch (e: Exception) {
            logger.error("Error trying to find session", e)
            null
        }

    override suspend fun saveSession(sessionEntry: SessionEntry): Boolean =
        try {
            val parentContext = coroutineContext
            withContext(dispatchersProvider.io()) {
                val sessionJson = Json.encodeToString(sessionEntry)
                syncCommands.setSession(sessionEntry, sessionJson)
                withContext(parentContext) {
                    true
                }
            }
        } catch (e: Exception) {
            logger.error("Error trying to save session", e)
            false
        }

    override suspend fun deleteSession(token: Token): Boolean =
        try {
            val parentContext = coroutineContext
            withContext(dispatchersProvider.io()) {
                syncCommands.deleteSession(Findable.ByToken(token))
                withContext(parentContext) {
                    true
                }
            }
        } catch (e: Exception) {
            logger.error("Error trying to delete session", e)
            false
        }

    private companion object {
        const val SESSION_BY_USER_ID_PREFIX = "session:userId:"
        const val SESSION_BY_TOKEN_PREFIX = "session:token:"

        fun Uuid.toKey(): String = SESSION_BY_USER_ID_PREFIX + value
        fun Token.toKey(): String = SESSION_BY_TOKEN_PREFIX + value

        fun Findable.toKey(): String =
            when (this) {
                is Findable.ByUserId -> userId.toKey()
                is Findable.ByToken -> token.toKey()
            }

        suspend fun RedisCoroutinesCommands<String, String>.getSession(findable: Findable): String? =
            get(findable.toKey())

        suspend fun RedisCoroutinesCommands<String, String>.setSession(session: SessionEntry, sessionJson: String) {
            set(session.userId.toKey(), sessionJson)
            set(session.encryptedToken.token.toKey(), sessionJson)
        }

        suspend fun RedisCoroutinesCommands<String, String>.deleteSession(findable: Findable) {
            del(findable.toKey())
        }
    }
}
