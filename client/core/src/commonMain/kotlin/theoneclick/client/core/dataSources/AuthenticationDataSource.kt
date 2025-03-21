package theoneclick.client.core.dataSources

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.*
import theoneclick.shared.core.models.endpoints.Endpoint
import theoneclick.shared.core.models.requests.RequestLoginRequest
import theoneclick.shared.core.models.responses.RequestLoginResponse
import theoneclick.shared.core.models.responses.UserLoggedResponse
import theoneclick.shared.core.models.results.RequestLoginResult
import theoneclick.shared.core.models.results.RequestLoginResult.UnknownError
import theoneclick.shared.core.models.results.RequestLoginResult.ValidLogin
import theoneclick.shared.core.models.results.UserLoggedResult
import theoneclick.client.core.idlingResources.IdlingResource
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
                Endpoint.IS_USER_LOGGED.route
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
            val response: RequestLoginResponse = client.post(
                Endpoint.REQUEST_LOGIN.route
            ) {
                contentType(ContentType.Application.Json)
                setBody(
                    RequestLoginRequest(
                        username = username,
                        password = password,
                    )
                )
            }.body()

            emit(response.toRequestLoginResult())
        }
            .onStart { idlingResource.increment() }
            .onCompletion { idlingResource.decrement() }
            .catch { emit(UnknownError) }
            .flowOn(dispatchersProvider.io())

    private fun UserLoggedResponse.toUserLoggedResult(): UserLoggedResult =
        when (this) {
            is UserLoggedResponse.Logged -> UserLoggedResult.Logged
            is UserLoggedResponse.NotLogged -> UserLoggedResult.NotLogged
            is UserLoggedResponse.UnknownError -> UserLoggedResult.UnknownError
        }

    private fun RequestLoginResponse.toRequestLoginResult(): RequestLoginResult =
        when (this) {
            is RequestLoginResponse.LocalRedirect -> ValidLogin.LocalRedirect(
                appRoute = appRoute
            )

            is RequestLoginResponse.ExternalRedirect -> ValidLogin.ExternalRedirect(
                urlString = urlString
            )
        }
}
