package theoneclick.server.shared.dataSources

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import theoneclick.server.shared.dataSources.base.SessionsDataSource
import theoneclick.server.shared.dataSources.base.SessionsDataSource.SessionEntry
import theoneclick.shared.contracts.core.models.Token
import theoneclick.shared.contracts.core.models.Uuid

class MemorySessionsDataSource : SessionsDataSource {
    private val sessionEntries = linkedMapOf<Uuid, SessionEntry>()
    private val mutex = Mutex()

    override suspend fun session(findable: SessionsDataSource.Findable): SessionEntry? =
        mutex.withLock {
            when (findable) {
                is SessionsDataSource.Findable.ByUserId -> sessionEntries[findable.userId]
                is SessionsDataSource.Findable.ByToken -> sessionEntries.values.firstOrNull { session ->
                    session.encryptedToken.token.value == findable.token.value
                }
            }
        }

    override suspend fun saveSession(sessionEntry: SessionEntry): Boolean {
        mutex.withLock {
            sessionEntries[sessionEntry.userId] = sessionEntry
            return true
        }
    }

    override suspend fun deleteSession(token: Token): Boolean {
        mutex.withLock {
            val userId = sessionEntries.values.firstOrNull { sessionEntry ->
                sessionEntry.encryptedToken.token.value == token.value
            }?.userId

            if (userId != null) {
                sessionEntries.remove(userId)
                return true
            }

            return false
        }
    }
}