package theoneclick.client.core.platform

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Test
import theoneclick.client.core.dataSources.AndroidInMemoryTokenDataSource
import theoneclick.client.core.models.results.RequestLoginResult
import theoneclick.client.core.models.results.UserLoggedResult
import theoneclick.client.core.navigation.RealNavigationController
import theoneclick.client.core.testing.TestData
import theoneclick.client.core.testing.fakes.fakeHttpClientEngine
import theoneclick.shared.testing.dispatchers.FakeDispatchersProvider
import kotlin.test.assertEquals

class AndroidRemoteAuthenticationDataSourceTest {
    private val navigationController = RealNavigationController()
    private val tokenDataSource = AndroidInMemoryTokenDataSource()
    private var isUserLogged = false
    private var isError = false
    private val httpClientEngine = fakeHttpClientEngine(
        isUserLogged = { isUserLogged },
        isError = { isError },
    )

    private val authenticationDataSource = AndroidRemoteAuthenticationDataSource(
        httpClient = androidHttpClient(
            httpClientEngine = httpClientEngine,
            tokenDataSource = tokenDataSource,
            navigationController = navigationController
        ),
        dispatchersProvider = FakeDispatchersProvider(Dispatchers.Main),
        tokenDataSource = tokenDataSource,
    )

    @Test
    fun `GIVEN user without token WHEN isUserLogged called THEN returns not logged`() {
        runTest {
            isUserLogged = true

            authenticationDataSource.isUserLogged().test {
                assertEquals(UserLoggedResult.NotLogged, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN user logged WHEN isUserLogged called THEN returns logged`() {
        runTest {
            isUserLogged = true
            tokenDataSource.set(TestData.TOKEN)

            authenticationDataSource.isUserLogged().test {
                assertEquals(UserLoggedResult.Logged, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN user not logged WHEN isUserLogged called THEN returns not logged`() {
        runTest {
            isUserLogged = false
            tokenDataSource.set(TestData.TOKEN)

            authenticationDataSource.isUserLogged().test {
                assertEquals(UserLoggedResult.NotLogged, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN server error WHEN isUserLogged called THEN returns unknown error`() {
        runTest {
            isUserLogged = true
            tokenDataSource.set(TestData.TOKEN)
            isError = true

            authenticationDataSource.isUserLogged().test {
                assertEquals(UserLoggedResult.UnknownError, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN valid data WHEN login THEN returns token`() {
        runTest {
            authenticationDataSource.login(username = TestData.USERNAME, password = TestData.PASSWORD).test {
                assertEquals(RequestLoginResult.ValidLogin, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            assertEquals(expected = TestData.TOKEN, actual = tokenDataSource.token())
        }
    }

    @Test
    fun `GIVEN invalid data WHEN login THEN returns failure`() {
        runTest {
            authenticationDataSource.login(username = "", password = "").test {
                assertEquals(RequestLoginResult.Failure, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN server error WHEN login THEN returns failure`() {
        runTest {
            isError = true

            authenticationDataSource.login(username = TestData.USERNAME, password = TestData.PASSWORD).test {
                assertEquals(RequestLoginResult.Failure, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            assertEquals(expected = null, actual = tokenDataSource.token())
        }
    }
}