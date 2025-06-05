package theoneclick.client.shared.network.platform

import kotlinx.coroutines.flow.Flow
import theoneclick.client.shared.network.models.LogoutResult
import theoneclick.client.shared.network.models.RequestLoginResult
import theoneclick.client.shared.network.models.UserLoggedResult

interface AuthenticationDataSource {
    fun isUserLogged(): Flow<UserLoggedResult>
    fun login(
        username: String,
        password: String
    ): Flow<RequestLoginResult>

    fun logout(): Flow<LogoutResult>
}
