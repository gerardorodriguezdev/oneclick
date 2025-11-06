package oneclick.client.shared.network.platform

import oneclick.client.shared.network.models.LogoutResult
import oneclick.client.shared.network.models.RequestLoginResult
import oneclick.client.shared.network.models.UserLoggedResult
import oneclick.shared.contracts.auth.models.requests.LoginRequest

interface AuthenticationDataSource {
    suspend fun isUserLogged(): UserLoggedResult
    suspend fun login(request: LoginRequest): RequestLoginResult
    suspend fun logout(): LogoutResult
}
