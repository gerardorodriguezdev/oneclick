package theoneclick.server.shared.repositories

import theoneclick.server.shared.dataSources.base.SessionsDataSource
import theoneclick.server.shared.dataSources.base.SessionsDataSource.Findable
import theoneclick.server.shared.dataSources.base.SessionsDataSource.SessionEntry
import theoneclick.server.shared.models.EncryptedToken
import theoneclick.shared.contracts.core.models.Token
import theoneclick.shared.contracts.core.models.Uuid

interface SessionsRepository {
    suspend fun session(findable: Findable): SessionEntry?
    suspend fun deleteSession(token: Token): Boolean
    suspend fun saveSession(userId: Uuid, encryptedToken: EncryptedToken): Boolean
}

class DefaultSessionsRepository(
    private val memorySessionsDataSource: SessionsDataSource,
    private val diskSessionsDataSource: SessionsDataSource,
) : SessionsRepository {

    override suspend fun session(findable: Findable): SessionEntry? {
        val memorySession = memorySessionsDataSource.session(findable)
        if (memorySession != null) return memorySession

        val diskSession = diskSessionsDataSource.session(findable)
        return if (diskSession != null) {
            memorySessionsDataSource.saveSession(diskSession)
            diskSession
        } else null
    }

    override suspend fun deleteSession(token: Token): Boolean {
        memorySessionsDataSource.deleteSession(token)
        return diskSessionsDataSource.deleteSession(token)
    }

    override suspend fun saveSession(
        userId: Uuid,
        encryptedToken: EncryptedToken
    ): Boolean {
        val session = SessionEntry(userId, encryptedToken)
        memorySessionsDataSource.saveSession(session)
        return diskSessionsDataSource.saveSession(session)
    }
}