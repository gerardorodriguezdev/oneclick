package theoneclick.client.shared.network.platform

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import theoneclick.client.shared.network.models.LogoutResult
import theoneclick.client.shared.network.models.RequestLoginResult
import theoneclick.client.shared.network.models.UserLoggedResult
import theoneclick.shared.contracts.core.endpoints.ClientEndpoint
import theoneclick.shared.contracts.core.requests.RequestLoginRequest
import theoneclick.shared.contracts.core.responses.UserLoggedResponse
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.logging.AppLogger

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
                else -> emit(RequestLoginResult.Error)
            }
        }
            .catch { exception ->
                appLogger.e("Exception catched '${exception.stackTraceToString()}' while requesting login '$username'")
                emit(RequestLoginResult.Error)
            }
            .flowOn(dispatchersProvider.io())

    override fun logout(): Flow<LogoutResult> =
        flow {
            val response = httpClient.get(ClientEndpoint.LOGOUT.route)

            when (response.status) {
                HttpStatusCode.OK -> emit(LogoutResult.Success)
                else -> emit(LogoutResult.Error)
            }
        }
            .catch { exception ->
                appLogger.e("Exception catched '${exception.stackTraceToString()}' while logging out")

                emit(LogoutResult.Error)
            }
            .flowOn(dispatchersProvider.io())
}
