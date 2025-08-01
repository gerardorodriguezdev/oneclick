package theoneclick.server.shared.dataSources

import theoneclick.server.shared.dataSources.base.SessionsDataSource
import theoneclick.server.shared.dataSources.base.SessionsDataSource.SessionEntry
import theoneclick.shared.contracts.core.models.Token
import theoneclick.shared.contracts.core.models.Uuid
import java.util.concurrent.ConcurrentHashMap

class MemorySessionsDataSource : SessionsDataSource {
    private val sessionEntries = ConcurrentHashMap<Uuid, SessionEntry>()

    override suspend fun session(findable: SessionsDataSource.Findable): SessionEntry? =
        when (findable) {
            is SessionsDataSource.Findable.ByUserId -> sessionEntries[findable.userId]
            is SessionsDataSource.Findable.ByToken -> sessionEntries.values.firstOrNull { session ->
                session.encryptedToken.token == findable.token
            }
        }

    override suspend fun saveSession(sessionEntry: SessionEntry): Boolean {
        val currentSize = sessionEntries.size


        if (currentSize > CLEAN_UP_LIMIT) {
            sessionEntries.clear()
        }

        sessionEntries[sessionEntry.userId] = sessionEntry
        return true
    }

    override suspend fun deleteSession(token: Token): Boolean {
        val userId = sessionEntries.values.firstOrNull { sessionEntry ->
            sessionEntry.encryptedToken.token == token
        }?.userId

        if (userId != null) {
            sessionEntries.remove(userId)
            return true
        }

        return false
    }

    private companion object {
        const val CLEAN_UP_LIMIT = 10_000
    }
}
