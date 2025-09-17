package theoneclick.client.shared.network.platform

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.withContext
import theoneclick.client.shared.network.models.LogoutResult
import theoneclick.client.shared.network.models.RequestLoginResult
import theoneclick.client.shared.network.models.UserLoggedResult
import theoneclick.shared.contracts.auth.models.requests.RequestLoginRequest
import theoneclick.shared.contracts.auth.models.responses.UserLoggedResponse
import theoneclick.shared.contracts.core.models.endpoints.ClientEndpoint
import theoneclick.shared.dispatchers.platform.DispatchersProvider
import theoneclick.shared.logging.AppLogger

class WasmRemoteAuthenticationDataSource(
    private val httpClient: HttpClient,
    private val dispatchersProvider: DispatchersProvider,
    private val appLogger: AppLogger,
) : AuthenticationDataSource {

    override suspend fun isUserLogged(): UserLoggedResult =
        withContext(dispatchersProvider.io()) {
            try {
                val response: UserLoggedResponse = httpClient.get(ClientEndpoint.IS_USER_LOGGED.route).body()
                response.toUserLoggedResult()
            } catch (error: Exception) {
                appLogger.e("Exception caught '${error.stackTraceToString()}' while checking if user is logged")
                UserLoggedResult.UnknownError
            }
        }

    private fun UserLoggedResponse.toUserLoggedResult(): UserLoggedResult =
        when (this) {
            is UserLoggedResponse.Logged -> UserLoggedResult.Logged
            is UserLoggedResponse.NotLogged -> UserLoggedResult.NotLogged
        }

    override suspend fun login(request: RequestLoginRequest): RequestLoginResult =
        withContext(dispatchersProvider.io()) {
            try {
                val response = httpClient.post(ClientEndpoint.REQUEST_LOGIN.route) {
                    setBody(request)
                }

                when (response.status) {
                    HttpStatusCode.OK -> RequestLoginResult.ValidLogin
                    else -> RequestLoginResult.Error
                }
            } catch (error: Exception) {
                appLogger.e(
                    "Exception caught '${error.stackTraceToString()}' " +
                            "while requesting logging user '${request.username.value}'"
                )
                RequestLoginResult.Error
            }
        }

    override suspend fun logout(): LogoutResult =
        withContext(dispatchersProvider.io()) {
            try {
                val response = httpClient.get(ClientEndpoint.LOGOUT.route)

                when (response.status) {
                    HttpStatusCode.OK -> LogoutResult.Success
                    else -> LogoutResult.Error
                }
            } catch (error: Exception) {
                appLogger.e("Exception caught '${error.stackTraceToString()}' while logging out")
                LogoutResult.Error
            }
        }
}
