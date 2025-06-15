package theoneclick.server.app.dataSources

import theoneclick.shared.contracts.core.dtos.TokenDto
import theoneclick.shared.contracts.core.dtos.TokenDto.Companion.toToken
import theoneclick.shared.timeProvider.TimeProvider

interface AuthenticationDataSource {
    fun isUserSessionValid(token: TokenDto): Boolean
    fun isUserSessionValid(token: String): Boolean
}

class DefaultAuthenticationDataSource(
    private val usersDataSource: UsersDataSource,
    private val timeProvider: TimeProvider,
) : AuthenticationDataSource {

    override fun isUserSessionValid(token: String): Boolean {
        val token = token.toToken() ?: return false
        return isUserSessionValid(token)
    }

    override fun isUserSessionValid(token: TokenDto): Boolean {
        val user = usersDataSource.user(token)

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