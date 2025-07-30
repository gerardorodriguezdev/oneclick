package theoneclick.server.shared.dataSources

import io.ktor.util.logging.*
import kotlinx.coroutines.withContext
import theoneclick.server.shared.dataSources.base.SessionsDataSource
import theoneclick.server.shared.models.EncryptedToken
import theoneclick.server.shared.postgresql.Sessions
import theoneclick.server.shared.postgresql.UsersDatabase
import theoneclick.shared.contracts.core.models.NonNegativeLong
import theoneclick.shared.contracts.core.models.Token
import theoneclick.shared.contracts.core.models.Uuid
import theoneclick.shared.dispatchers.platform.DispatchersProvider

class PostgresSessionsDataSource(
    private val database: UsersDatabase,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
) : SessionsDataSource {

    override suspend fun session(findable: SessionsDataSource.Findable): SessionsDataSource.SessionEntry? =
        try {
            withContext(dispatchersProvider.io()) {
                val dbSession = when (findable) {
                    is SessionsDataSource.Findable.ByUserId -> {
                        database.sessionsQueries
                            .sessionByUserId(findable.userId.value)
                            .executeAsOneOrNull()
                    }

                    is SessionsDataSource.Findable.ByToken -> {
                        database.sessionsQueries
                            .sessionBySessionToken(findable.token.value)
                            .executeAsOneOrNull()
                    }
                }

                dbSession?.toSessionEntry()
            }
        } catch (e: Exception) {
            logger.error("Error trying to find session", e)
            null
        }

    private fun Sessions.toSessionEntry(): SessionsDataSource.SessionEntry =
        SessionsDataSource.SessionEntry(
            userId = Uuid.unsafe(requireNotNull(user_id)),
            encryptedToken = EncryptedToken(
                token = Token.unsafe(session_token),
                creationTimeInMillis = NonNegativeLong.unsafe(creation_time)
            )
        )

    override suspend fun saveSession(sessionEntry: SessionsDataSource.SessionEntry): Boolean =
        try {
            withContext(dispatchersProvider.io()) {
                database.sessionsQueries.insertSession(sessionEntry.toSessions())
                true
            }
        } catch (e: Exception) {
            logger.error("Error trying to save session", e)
            false
        }

    private fun SessionsDataSource.SessionEntry.toSessions(): Sessions =
        Sessions(
            user_id = userId.value,
            session_token = encryptedToken.token.value,
            creation_time = encryptedToken.creationTimeInMillis.value,
        )

    override suspend fun deleteSession(token: Token): Boolean =
        try {
            withContext(dispatchersProvider.io()) {
                database.sessionsQueries.deleteBySessionToken(token.value)
                true
            }
        } catch (e: Exception) {
            logger.error("Error trying to delete session", e)
            false
        }
}
