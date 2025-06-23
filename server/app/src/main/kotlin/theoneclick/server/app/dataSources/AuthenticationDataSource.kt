package theoneclick.server.app.dataSources

import me.tatarka.inject.annotations.Inject
import theoneclick.server.app.dataSources.base.UsersDataSource
import theoneclick.server.app.repositories.UsersRepository
import theoneclick.shared.contracts.core.models.Token
import theoneclick.shared.contracts.core.models.Token.Companion.toToken
import theoneclick.shared.timeProvider.TimeProvider

interface AuthenticationDataSource {
    fun isUserSessionValid(token: Token): Boolean
    fun isUserSessionValid(token: String): Boolean
}

@Inject
class DefaultAuthenticationDataSource(
    private val usersRepository: UsersRepository,
    private val timeProvider: TimeProvider,
) : AuthenticationDataSource {

    override fun isUserSessionValid(token: String): Boolean {
        val token = token.toToken() ?: return false
        return isUserSessionValid(token)
    }

    override fun isUserSessionValid(token: Token): Boolean {
        val user = usersRepository.user(UsersDataSource.Findable.ByToken(token))

        return when {
            user == null -> false
            user.sessionToken == null -> false

            timeProvider.currentTimeMillis() > user.sessionToken.creationTimeInMillis +
                    USER_SESSION_TOKEN_EXPIRATION_IN_MILLIS -> false

            user.sessionToken.token.value != token.value -> false
            else -> true
        }
    }

    companion object {
        const val USER_SESSION_TOKEN_EXPIRATION_IN_MILLIS = 3_600_000L
    }
}