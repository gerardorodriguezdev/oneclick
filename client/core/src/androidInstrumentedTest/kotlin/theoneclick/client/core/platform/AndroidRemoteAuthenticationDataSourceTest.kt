package theoneclick.client.core.platform

import app.cash.turbine.test
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import theoneclick.client.core.dataSources.EmptyTokenDataSource
import theoneclick.client.core.idlingResources.EmptyIdlingResource
import theoneclick.client.core.models.results.RequestLoginResult
import theoneclick.client.core.models.results.UserLoggedResult
import theoneclick.client.core.navigation.RealNavigationController
import theoneclick.shared.core.models.endpoints.ClientEndpoint
import theoneclick.shared.core.models.requests.RequestLoginRequest
import theoneclick.shared.core.models.responses.UserLoggedResponse
import theoneclick.shared.testing.dispatchers.FakeDispatchersProvider
import theoneclick.shared.testing.extensions.mockEngine
import theoneclick.shared.testing.extensions.respondJson
import theoneclick.shared.testing.extensions.toRequestBodyObject
import kotlin.test.assertEquals

//TODO: Fix
class AndroidRemoteAuthenticationDataSourceTest {

    @Test
    fun `GIVEN user logged WHEN isUserLogged called THEN returns logged`() {
        runTest {
            val authenticationDataSource =
                authenticationDataSource(httpClient = userLoggedEndpointMockHttpClient(UserLoggedResponse.Logged))

            authenticationDataSource.isUserLogged().test {
                assertEquals(UserLoggedResult.Logged, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN user not logged WHEN isUserLogged called THEN returns not logged`() {
        runTest {
            val authenticationDataSource =
                this.authenticationDataSource(httpClient = userLoggedEndpointMockHttpClient(UserLoggedResponse.NotLogged))

            authenticationDataSource.isUserLogged().test {
                assertEquals(UserLoggedResult.NotLogged, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN server error WHEN isUserLogged called THEN returns unknown error`() {
        runTest {
            val authenticationDataSource = authenticationDataSource(
                httpClient = requestLoginEndpointMockHttpClient(
                    mockEngine(
                        pathToFake = ClientEndpoint.IS_USER_LOGGED.route,
                        onPathFound = { respondError(HttpStatusCode.Companion.BadRequest) },
                    )
                )
            )

            authenticationDataSource.isUserLogged().test {
                assertEquals(UserLoggedResult.UnknownError, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN valid data with local redirect WHEN login THEN returns validLocalRedirect`() {
        runTest {
            val authenticationDataSource = authenticationDataSource(
                httpClient = requestLoginEndpointMockHttpClient()
            )

            authenticationDataSource.login(username = USERNAME, password = PASSWORD).test {
                assertEquals(RequestLoginResult.ValidLogin, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN invalid data WHEN login THEN returns unknown error`() {
        runTest {
            val authenticationDataSource = authenticationDataSource(
                httpClient = requestLoginEndpointMockHttpClient()
            )

            authenticationDataSource.login(username = "", password = "").test {
                assertEquals(RequestLoginResult.Failure, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN error WHEN login THEN returns unknown error`() {
        runTest {
            val authenticationDataSource = authenticationDataSource(
                httpClient = requestLoginEndpointMockHttpClient(
                    mockEngine(
                        pathToFake = ClientEndpoint.REQUEST_LOGIN.route,
                        onPathFound = { respondError(HttpStatusCode.Companion.InternalServerError) },
                    )
                )
            )

            authenticationDataSource.login(username = USERNAME, password = PASSWORD).test {
                assertEquals(RequestLoginResult.Failure, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    companion object {
        const val USERNAME = "Username1"
        const val PASSWORD = "Password1"

        private fun TestScope.authenticationDataSource(httpClient: HttpClient): AuthenticationDataSource =
            AndroidRemoteAuthenticationDataSource(
                httpClient = httpClient,
                dispatchersProvider = FakeDispatchersProvider(StandardTestDispatcher(testScheduler)),
                tokenDataSource = EmptyTokenDataSource(),
            )

        private fun userLoggedEndpointMockHttpClient(result: UserLoggedResponse): HttpClient =
            requestLoginEndpointMockHttpClient(
                mockEngine(
                    pathToFake = ClientEndpoint.IS_USER_LOGGED.route,
                    onPathFound = {
                        respondJson<UserLoggedResponse>(result)
                    },
                )
            )

        private fun requestLoginEndpointMockHttpClient(): HttpClient =
            requestLoginEndpointMockHttpClient(
                mockEngine(
                    pathToFake = ClientEndpoint.REQUEST_LOGIN.route,
                    onPathFound = { request ->
                        val requestLoginRequest = request.toRequestBodyObject<RequestLoginRequest>()

                        when {
                            requestLoginRequest == null -> respondError(HttpStatusCode.Companion.BadRequest)
                            requestLoginRequest.username != USERNAME -> respondError(HttpStatusCode.Companion.BadRequest)
                            requestLoginRequest.password != PASSWORD -> respondError(HttpStatusCode.Companion.BadRequest)
                            else -> respondOk()
                        }
                    },
                )
            )

        private fun requestLoginEndpointMockHttpClient(httpClientEngine: HttpClientEngine): HttpClient =
            androidHttpClient(
                httpClientEngine = httpClientEngine,
                tokenDataSource = EmptyTokenDataSource(),
                idlingResource = EmptyIdlingResource(),
                navigationController = RealNavigationController(),
            )
    }
}