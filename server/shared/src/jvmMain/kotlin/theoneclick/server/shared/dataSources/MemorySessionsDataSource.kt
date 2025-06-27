package theoneclick.server.shared.dataSources

import theoneclick.server.shared.dataSources.base.SessionsDataSource
import theoneclick.server.shared.dataSources.base.SessionsDataSource.SessionEntry
import theoneclick.shared.contracts.core.models.Token
import theoneclick.shared.contracts.core.models.Uuid

class MemorySessionsDataSource : SessionsDataSource {
    private val sessionEntries = linkedMapOf<Uuid, SessionEntry>()

    override fun session(findable: SessionsDataSource.Findable): SessionEntry? =
        when (findable) {
            is SessionsDataSource.Findable.ByUserId -> sessionEntries[findable.userId]
            is SessionsDataSource.Findable.ByToken -> sessionEntries.values.firstOrNull { session ->
                session.encryptedToken.token.value == findable.token.value
            }
        }

    override fun saveSession(sessionEntry: SessionEntry) {
        sessionEntries[sessionEntry.userId] = sessionEntry
    }

    override fun deleteSession(token: Token) {
        val userId = sessionEntries.values.firstOrNull { sessionEntry ->
            sessionEntry.encryptedToken.token.value == token.value
        }?.userId

        if (userId != null) {
            sessionEntries.remove(userId)
        }
    }
}