package theoneclick.client.core.platform

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import theoneclick.client.core.models.results.LogoutResult
import theoneclick.client.core.models.results.RequestLoginResult
import theoneclick.client.core.models.results.UserLoggedResult
import theoneclick.shared.core.models.endpoints.ClientEndpoint
import theoneclick.shared.core.models.requests.RequestLoginRequest
import theoneclick.shared.core.models.responses.UserLoggedResponse
import theoneclick.shared.core.platform.AppLogger
import theoneclick.shared.dispatchers.platform.DispatchersProvider

class WasmRemoteAuthenticationDataSource(
    private val httpClient: HttpClient,
    private val dispatchersProvider: DispatchersProvider,
    private val appLogger: AppLogger,
) : AuthenticationDataSource {

    override fun isUserLogged(): Flow<UserLoggedResult> =
        flow {
            val response: UserLoggedResponse = httpClient.get(ClientEndpoint.IS_USER_LOGGED.route).body()
            emit(response.toUserLoggedResult())
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
        flow {
            val response = httpClient.post(ClientEndpoint.REQUEST_LOGIN.route) {
                setBody(
                    RequestLoginRequest(
                        username = username,
                        password = password,
                    )
                )
            }

            when (response.status) {
                HttpStatusCode.OK -> emit(RequestLoginResult.ValidLogin)
                else -> emit(RequestLoginResult.Failure)
            }
        }
            .catch { exception ->
                appLogger.e("Exception catched '${exception.stackTraceToString()}' while requesting login '$username'")
                emit(RequestLoginResult.Failure)
            }
            .flowOn(dispatchersProvider.io())

    override fun logout(): Flow<LogoutResult> =
        flow {
            val response = httpClient.get(ClientEndpoint.LOGOUT.route)

            when (response.status) {
                HttpStatusCode.OK -> emit(LogoutResult.Success)
                else -> emit(LogoutResult.Failure)
            }
        }
            .catch { exception ->
                appLogger.e("Exception catched '${exception.stackTraceToString()}' while logging out")

                emit(LogoutResult.Failure)
            }
            .flowOn(dispatchersProvider.io())
}
