package theoneclick.client.shared.network.platform

import theoneclick.client.shared.network.models.LogoutResult
import theoneclick.client.shared.network.models.RequestLoginResult
import theoneclick.client.shared.network.models.UserLoggedResult
import theoneclick.shared.contracts.core.models.requests.RequestLoginRequest

interface AuthenticationDataSource {
    suspend fun isUserLogged(): UserLoggedResult
    suspend fun login(request: RequestLoginRequest): RequestLoginResult
    suspend fun logout(): LogoutResult
}
