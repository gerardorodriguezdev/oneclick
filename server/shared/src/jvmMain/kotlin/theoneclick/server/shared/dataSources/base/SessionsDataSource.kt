package theoneclick.server.shared.dataSources.base

import theoneclick.server.shared.models.EncryptedToken
import theoneclick.shared.contracts.core.models.Token
import theoneclick.shared.contracts.core.models.Uuid

interface SessionsDataSource {
    suspend fun session(findable: Findable): SessionEntry?
    suspend fun saveSession(sessionEntry: SessionEntry): Boolean
    suspend fun deleteSession(token: Token): Boolean

    sealed interface Findable {
        data class ByUserId(val userId: Uuid) : Findable
        data class ByToken(val token: Token) : Findable
    }

    data class SessionEntry(
        val userId: Uuid,
        val encryptedToken: EncryptedToken,
    )
}