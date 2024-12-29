package theoneclick.client.core.testing.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import theoneclick.shared.core.dataSources.AuthenticationDataSource
import theoneclick.shared.core.dataSources.models.results.RequestLoginResult
import theoneclick.shared.core.dataSources.models.results.UserLoggedResult

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
