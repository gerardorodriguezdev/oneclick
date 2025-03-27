package theoneclick.client.core.platform

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import theoneclick.client.core.dataSources.TokenDataSource
import theoneclick.client.core.models.results.RequestLoginResult
import theoneclick.client.core.models.results.UserLoggedResult
import theoneclick.shared.core.models.endpoints.ClientEndpoint
import theoneclick.shared.core.models.requests.RequestLoginRequest
import theoneclick.shared.core.models.responses.RequestLoginResponse
import theoneclick.shared.core.models.responses.UserLoggedResponse
import theoneclick.shared.core.platform.AppLogger
import theoneclick.shared.dispatchers.platform.DispatchersProvider

class AndroidRemoteAuthenticationDataSource(
    private val httpClient: HttpClient,
    private val dispatchersProvider: DispatchersProvider,
    private val tokenDataSource: TokenDataSource,
    private val appLogger: AppLogger,
) : AuthenticationDataSource {

    override fun isUserLogged(): Flow<UserLoggedResult> =
        flow {
            val token = tokenDataSource.token()
            if (token == null) {
                emit(UserLoggedResult.NotLogged)
                return@flow
            }

            val response = httpClient.get(ClientEndpoint.IS_USER_LOGGED.route)
            when (response.status) {
                HttpStatusCode.OK -> {
                    val isUserLoggedResponse: UserLoggedResponse = response.body()
                    emit(isUserLoggedResponse.toUserLoggedResult())
                }

                else -> emit(UserLoggedResult.UnknownError)
            }
        }
            .catch { exception ->
                appLogger.e("Exception catched '${exception.stackTraceToString()}' while checking if user is logged")
                emit(UserLoggedResult.UnknownError)
            }
            .flowOn(dispatchersProvider.io())

    private fun UserLoggedResponse.toUserLoggedResult(): UserLoggedResult =
        when (this) {
            is UserLoggedResponse.Logged -> UserLoggedResult.Logged
            is UserLoggedResponse.NotLogged -> UserLoggedResult.NotLogged
        }

    override fun login(
        username: String,
        password: String
    ): Flow<RequestLoginResult> =
        flow<RequestLoginResult> {
            val response = httpClient.post(ClientEndpoint.REQUEST_LOGIN.route) {
                setBody(
                    RequestLoginRequest(
                        username = username,
                        password = password,
                    )
                )
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val requestLoginResponse: RequestLoginResponse = response.body()
                    tokenDataSource.set(requestLoginResponse.token)
                    emit(RequestLoginResult.ValidLogin)
                }

                else -> emit(RequestLoginResult.Failure)
            }
        }
            .catch { exception ->
                appLogger.e("Exception catched '${exception.stackTraceToString()}' while requesting logging user '$username'")
                emit(RequestLoginResult.Failure)
            }
            .flowOn(dispatchersProvider.io())
}