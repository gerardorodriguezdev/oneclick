package theoneclick.server.shared.repositories

import theoneclick.server.shared.dataSources.base.SessionsDataSource
import theoneclick.server.shared.dataSources.base.SessionsDataSource.Findable
import theoneclick.server.shared.dataSources.base.SessionsDataSource.SessionEntry
import theoneclick.server.shared.models.EncryptedToken
import theoneclick.shared.contracts.core.models.Token
import theoneclick.shared.contracts.core.models.Uuid

interface SessionsRepository {
    fun session(findable: Findable): SessionEntry?
    fun deleteSession(token: Token)
    fun saveSession(userId: Uuid, encryptedToken: EncryptedToken)
}

class DefaultSessionsRepository(
    private val memorySessionsDataSource: SessionsDataSource,
    private val diskSessionsDataSource: SessionsDataSource,
) : SessionsRepository {

    override fun session(findable: Findable): SessionEntry? {
        val memorySession = memorySessionsDataSource.session(findable)
        if (memorySession != null) return memorySession

        val diskSession = diskSessionsDataSource.session(findable)
        return if (diskSession != null) {
            memorySessionsDataSource.saveSession(diskSession)
            diskSession
        } else null
    }

    override fun deleteSession(token: Token) {
        memorySessionsDataSource.deleteSession(token)
        diskSessionsDataSource.deleteSession(token)
    }

    override fun saveSession(
        userId: Uuid,
        encryptedToken: EncryptedToken
    ) {
        val session = SessionEntry(userId, encryptedToken)
        memorySessionsDataSource.saveSession(session)
        diskSessionsDataSource.saveSession(session)
    }
}