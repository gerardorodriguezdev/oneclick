package theoneclick.client.app.platform

import kotlinx.coroutines.flow.Flow
import theoneclick.client.app.models.results.LogoutResult
import theoneclick.client.app.models.results.RequestLoginResult
import theoneclick.client.app.models.results.UserLoggedResult

interface AuthenticationDataSource {
    fun isUserLogged(): Flow<UserLoggedResult>
    fun login(
        username: String,
        password: String
    ): Flow<RequestLoginResult>
    fun logout(): Flow<LogoutResult>
}
