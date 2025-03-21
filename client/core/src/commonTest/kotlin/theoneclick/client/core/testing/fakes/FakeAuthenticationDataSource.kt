package theoneclick.client.core.testing.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import theoneclick.client.core.dataSources.AuthenticationDataSource
import theoneclick.client.core.models.results.RequestLoginResult
import theoneclick.client.core.models.results.UserLoggedResult

class FakeAuthenticationDataSource(
    var userLoggedResult: Flow<UserLoggedResult> = flowOf(),
    var requestLoginResultFlow: Flow<RequestLoginResult> = flowOf(),
) : AuthenticationDataSource {

    override fun isUserLogged(): Flow<UserLoggedResult> =
        userLoggedResult

    override fun requestLogin(
        username: String,
        password: String
    ): Flow<RequestLoginResult> = requestLoginResultFlow
}
