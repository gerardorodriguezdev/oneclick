package theoneclick.client.core.testing.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import theoneclick.client.core.models.results.LogoutResult
import theoneclick.client.core.platform.AuthenticationDataSource
import theoneclick.client.core.models.results.RequestLoginResult
import theoneclick.client.core.models.results.UserLoggedResult

class FakeAuthenticationDataSource(
    var userLoggedResult: Flow<UserLoggedResult> = flowOf(),
    var requestLoginResultFlow: Flow<RequestLoginResult> = flowOf(),
    var logoutResultFlow: Flow<LogoutResult> = flowOf(),
) : AuthenticationDataSource {

    override fun isUserLogged(): Flow<UserLoggedResult> =
        userLoggedResult

    override fun login(
        username: String,
        password: String
    ): Flow<RequestLoginResult> = requestLoginResultFlow

    override fun logout(): Flow<LogoutResult> = logoutResultFlow
}
