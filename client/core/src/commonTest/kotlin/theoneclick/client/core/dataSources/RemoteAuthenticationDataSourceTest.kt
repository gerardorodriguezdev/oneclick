package theoneclick.client.core.dataSources

import app.cash.turbine.test
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import theoneclick.client.core.idlingResources.EmptyIdlingResource
import theoneclick.client.core.models.results.RequestLoginResult
import theoneclick.client.core.models.results.UserLoggedResult
import theoneclick.shared.core.extensions.defaultHttpClient
import theoneclick.shared.core.models.endpoints.ClientEndpoints
import theoneclick.shared.core.models.requests.RequestLoginRequest
import theoneclick.shared.core.models.responses.UserLoggedResponse
import theoneclick.shared.testing.dispatchers.FakeDispatchersProvider
import theoneclick.shared.testing.extensions.mockEngine
import theoneclick.shared.testing.extensions.respondJson
import theoneclick.shared.testing.extensions.toRequestBodyObject
import kotlin.test.Test
import kotlin.test.assertEquals

class RemoteAuthenticationDataSourceTest {

    @Test
    fun `GIVEN user logged WHEN isUserLogged called THEN returns logged`() {
        runTest {
            val remoteAuthenticationDataSource =
                remoteAuthenticationDataSource(client = userLoggedEndpointMockHttpClient(UserLoggedResponse.Logged))

            remoteAuthenticationDataSource.isUserLogged().test {
                assertEquals(UserLoggedResult.Logged, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN user not logged WHEN isUserLogged called THEN returns not logged`() {
        runTest {
            val remoteAuthenticationDataSource =
                remoteAuthenticationDataSource(client = userLoggedEndpointMockHttpClient(UserLoggedResponse.NotLogged))

            remoteAuthenticationDataSource.isUserLogged().test {
                assertEquals(UserLoggedResult.NotLogged, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN server error WHEN isUserLogged called THEN returns unknown error`() {
        runTest {
            val remoteAuthenticationDataSource = remoteAuthenticationDataSource(
                client = defaultHttpClient(
                    mockEngine(
                        pathToFake = ClientEndpoints.IS_USER_LOGGED.route,
                        onPathFound = { respondError(HttpStatusCode.BadRequest) },
                    )
                )
            )

            remoteAuthenticationDataSource.isUserLogged().test {
                assertEquals(UserLoggedResult.UnknownError, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN valid data with local redirect WHEN requestLogin THEN returns validLocalRedirect`() {
        runTest {
            val remoteAuthenticationDataSource = remoteAuthenticationDataSource(
                client = requestLoginEndpointMockHttpClient()
            )

            remoteAuthenticationDataSource.requestLogin(username = USERNAME, password = PASSWORD).test {
                assertEquals(RequestLoginResult.ValidLogin, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN invalid data WHEN requestLogin THEN returns unknown error`() {
        runTest {
            val remoteAuthenticationDataSource = remoteAuthenticationDataSource(
                client = requestLoginEndpointMockHttpClient()
            )

            remoteAuthenticationDataSource.requestLogin(username = "", password = "").test {
                assertEquals(RequestLoginResult.UnknownError, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN error WHEN requestLogin THEN returns unknown error`() {
        runTest {
            val remoteAuthenticationDataSource = remoteAuthenticationDataSource(
                client = defaultHttpClient(
                    mockEngine(
                        pathToFake = ClientEndpoints.REQUEST_LOGIN.route,
                        onPathFound = { respondError(HttpStatusCode.InternalServerError) },
                    )
                )
            )

            remoteAuthenticationDataSource.requestLogin(username = USERNAME, password = PASSWORD).test {
                assertEquals(RequestLoginResult.UnknownError, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    companion object {
        const val USERNAME = "Username1"
        const val PASSWORD = "Password1"

        private fun TestScope.remoteAuthenticationDataSource(client: HttpClient): RemoteAuthenticationDataSource =
            RemoteAuthenticationDataSource(
                dispatchersProvider = FakeDispatchersProvider(StandardTestDispatcher(testScheduler)),
                client = client,
                idlingResource = EmptyIdlingResource(),
            )

        private fun userLoggedEndpointMockHttpClient(result: UserLoggedResponse): HttpClient =
            defaultHttpClient(
                mockEngine(
                    pathToFake = ClientEndpoints.IS_USER_LOGGED.route,
                    onPathFound = {
                        respondJson<UserLoggedResponse>(result)
                    },
                )
            )

        private fun requestLoginEndpointMockHttpClient(): HttpClient =
            defaultHttpClient(
                mockEngine(
                    pathToFake = ClientEndpoints.REQUEST_LOGIN.route,
                    onPathFound = { request ->
                        val requestLoginRequest = request.toRequestBodyObject<RequestLoginRequest>()

                        when {
                            requestLoginRequest == null -> respondError(HttpStatusCode.BadRequest)
                            requestLoginRequest.username != USERNAME -> respondError(HttpStatusCode.BadRequest)
                            requestLoginRequest.password != PASSWORD -> respondError(HttpStatusCode.BadRequest)
                            else -> respondOk()
                        }
                    },
                )
            )

        private fun defaultHttpClient(mockEngine: MockEngine): HttpClient =
            defaultHttpClient(engine = mockEngine, protocol = null, host = null, port = null)
    }
}
