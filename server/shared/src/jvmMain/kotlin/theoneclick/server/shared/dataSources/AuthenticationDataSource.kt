package theoneclick.server.shared.dataSources

import me.tatarka.inject.annotations.Inject
import theoneclick.server.shared.dataSources.base.SessionsDataSource.Findable
import theoneclick.server.shared.repositories.SessionsRepository
import theoneclick.shared.contracts.core.models.Token
import theoneclick.shared.contracts.core.models.Token.Companion.toToken
import theoneclick.shared.timeProvider.TimeProvider

interface AuthenticationDataSource {
    suspend fun isUserSessionValid(token: Token): Boolean
    suspend fun isUserSessionValid(token: String): Boolean
}

@Inject
class DefaultAuthenticationDataSource(
    private val sessionsRepository: SessionsRepository,
    private val timeProvider: TimeProvider,
) : AuthenticationDataSource {

    override suspend fun isUserSessionValid(token: String): Boolean {
        val token = token.toToken() ?: return false
        return isUserSessionValid(token)
    }

    override suspend fun isUserSessionValid(token: Token): Boolean {
        val session = sessionsRepository.session(Findable.ByToken(token))
        val sessionToken = session?.encryptedToken

        return when {
            sessionToken == null -> false

            timeProvider.currentTimeMillis() > sessionToken.creationTimeInMillis.value +
                    USER_SESSION_TOKEN_EXPIRATION_IN_MILLIS -> false

            sessionToken.token != token -> false
            else -> true
        }
    }

    companion object {
        const val USER_SESSION_TOKEN_EXPIRATION_IN_MILLIS = 3_600_000L
    }
}
