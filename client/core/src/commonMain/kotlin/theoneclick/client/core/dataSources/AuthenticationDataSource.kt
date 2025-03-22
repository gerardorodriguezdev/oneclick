package theoneclick.client.core.dataSources

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.*
import theoneclick.client.core.idlingResources.IdlingResource
import theoneclick.client.core.models.results.RequestLoginResult
import theoneclick.client.core.models.results.RequestLoginResult.UnknownError
import theoneclick.client.core.models.results.RequestLoginResult.ValidLogin
import theoneclick.client.core.models.results.UserLoggedResult
import theoneclick.shared.core.models.endpoints.ClientEndpoints
import theoneclick.shared.core.models.requests.RequestLoginRequest
import theoneclick.shared.core.models.responses.UserLoggedResponse
import theoneclick.shared.dispatchers.platform.DispatchersProvider

interface AuthenticationDataSource {
    fun isUserLogged(): Flow<UserLoggedResult>
    fun requestLogin(
        username: String,
        password: String
    ): Flow<RequestLoginResult>
}

class RemoteAuthenticationDataSource(
    private val client: HttpClient,
    private val dispatchersProvider: DispatchersProvider,
    private val idlingResource: IdlingResource,
) : AuthenticationDataSource {

    override fun isUserLogged(): Flow<UserLoggedResult> =
        flow {
            val response: UserLoggedResponse = client.get(
                ClientEndpoints.IS_USER_LOGGED.route
            ).body()
            emit(response.toUserLoggedResult())
        }
            .onStart { idlingResource.increment() }
            .onCompletion { idlingResource.decrement() }
            .catch { emit(UserLoggedResult.UnknownError) }
            .flowOn(dispatchersProvider.io())

    override fun requestLogin(
        username: String,
        password: String
    ): Flow<RequestLoginResult> =
        flow {
            val response = client.post(ClientEndpoints.REQUEST_LOGIN.route) {
                contentType(ContentType.Application.Json)
                setBody(
                    RequestLoginRequest(
                        username = username,
                        password = password,
                    )
                )
            }

            when (response.status) {
                HttpStatusCode.OK -> emit(ValidLogin)
                else -> emit(UnknownError)
            }
        }
            .onStart { idlingResource.increment() }
            .onCompletion { idlingResource.decrement() }
            .catch { emit(UnknownError) }
            .flowOn(dispatchersProvider.io())

    private fun UserLoggedResponse.toUserLoggedResult(): UserLoggedResult =
        when (this) {
            is UserLoggedResponse.Logged -> UserLoggedResult.Logged
            is UserLoggedResponse.NotLogged -> UserLoggedResult.NotLogged
        }
}
