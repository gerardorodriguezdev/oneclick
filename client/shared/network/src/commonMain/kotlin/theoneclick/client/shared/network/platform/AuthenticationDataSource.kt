package theoneclick.client.shared.network.platform

import kotlinx.coroutines.flow.Flow
import theoneclick.client.shared.network.models.LogoutResult
import theoneclick.client.shared.network.models.RequestLoginResult
import theoneclick.client.shared.network.models.UserLoggedResult
import theoneclick.shared.contracts.core.models.requests.RequestLoginRequestDto

interface AuthenticationDataSource {
    fun isUserLogged(): Flow<UserLoggedResult>
    fun login(request: RequestLoginRequestDto): Flow<RequestLoginResult>
    fun logout(): Flow<LogoutResult>
}
