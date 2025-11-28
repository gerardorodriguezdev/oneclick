package oneclick.client.shared.network.dataSources

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.withContext
import oneclick.client.shared.network.models.LogoutResult
import oneclick.client.shared.network.models.RequestLoginResult
import oneclick.client.shared.network.models.UserLoggedResult
import oneclick.client.shared.network.platform.AuthenticationDataSource
import oneclick.shared.contracts.auth.models.requests.LoginRequest
import oneclick.shared.contracts.auth.models.responses.HomeRequestLoginResponse
import oneclick.shared.contracts.auth.models.responses.IsLoggedResponse
import oneclick.shared.contracts.auth.models.responses.MobileRequestLoginResponse
import oneclick.shared.contracts.core.models.ClientEndpoint
import oneclick.shared.dispatchers.platform.DispatchersProvider
import oneclick.shared.logging.AppLogger

class RemoteAuthenticationDataSource(
    private val httpClient: HttpClient,
    private val dispatchersProvider: DispatchersProvider,
    private val tokenDataSource: TokenDataSource,
    private val appLogger: AppLogger,
) : AuthenticationDataSource {

    override suspend fun isUserLogged(): UserLoggedResult =
        withContext(dispatchersProvider.io()) {
            try {
                if (tokenDataSource.token() == null) {
                    return@withContext UserLoggedResult.NotLogged
                }

                val response = httpClient.get(ClientEndpoint.IS_LOGGED.route)
                when (response.status) {
                    HttpStatusCode.OK -> {
                        val isLoggedResponse: IsLoggedResponse = response.body()
                        isLoggedResponse.toUserLoggedResult()
                    }

                    else -> UserLoggedResult.UnknownError
                }
            } catch (error: Exception) {
                appLogger.e("Exception '${error.stackTraceToString()}' while checking if user is logged")
                UserLoggedResult.UnknownError
            }
        }

    private fun IsLoggedResponse.toUserLoggedResult(): UserLoggedResult =
        when (this) {
            is IsLoggedResponse.Logged -> UserLoggedResult.Logged
            is IsLoggedResponse.NotLogged -> UserLoggedResult.NotLogged
        }

    override suspend fun login(request: LoginRequest): RequestLoginResult =
        withContext(dispatchersProvider.io()) {
            try {
                val route = when (request) {
                    is LoginRequest.UserRequestLoginRequest -> ClientEndpoint.USER_REQUEST_LOGIN.route
                    is LoginRequest.HomeRequestLoginRequest -> ClientEndpoint.HOME_REQUEST_LOGIN.route
                }

                val response = httpClient.post(route) {
                    setBody(request)
                }

                when (response.status) {
                    HttpStatusCode.OK -> {
                        when (request) {
                            is LoginRequest.UserRequestLoginRequest ->
                                response.body<MobileRequestLoginResponse>().handle()

                            is LoginRequest.HomeRequestLoginRequest ->
                                response.body<HomeRequestLoginResponse>().handle()
                        }
                    }

                    else -> RequestLoginResult.Error
                }
            } catch (error: Exception) {
                appLogger.e(
                    "Exception '${error.stackTraceToString()}' " +
                            "while requesting logging user '${request.username.value}'"
                )
                RequestLoginResult.Error
            }
        }

    private suspend fun MobileRequestLoginResponse.handle(): RequestLoginResult =
        when (this) {
            is MobileRequestLoginResponse.ValidLogin -> {
                tokenDataSource.set(jwt.value)
                RequestLoginResult.ValidLogin
            }

            is MobileRequestLoginResponse.WaitForApproval -> RequestLoginResult.WaitForApproval
        }

    private suspend fun HomeRequestLoginResponse.handle(): RequestLoginResult =
        when (this) {
            is HomeRequestLoginResponse.ValidLogin -> {
                tokenDataSource.set(jwt.value)
                RequestLoginResult.ValidLogin
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
                appLogger.e("Exception '${error.stackTraceToString()}' while logging out")

                LogoutResult.Error
            }
        }
}