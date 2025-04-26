package theoneclick.client.core.platform

import kotlinx.coroutines.flow.Flow
import theoneclick.client.core.models.results.LogoutResult
import theoneclick.client.core.models.results.RequestLoginResult
import theoneclick.client.core.models.results.UserLoggedResult

interface AuthenticationDataSource {
    fun isUserLogged(): Flow<UserLoggedResult>
    fun login(
        username: String,
        password: String
    ): Flow<RequestLoginResult>
    fun logout(): Flow<LogoutResult>
}
