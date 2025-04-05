package theoneclick.client.core.platform

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Test
import theoneclick.client.core.dataSources.AndroidInMemoryTokenDataSource
import theoneclick.client.core.models.results.LogoutResult
import theoneclick.client.core.models.results.RequestLoginResult
import theoneclick.client.core.models.results.UserLoggedResult
import theoneclick.client.core.navigation.RealNavigationController
import theoneclick.client.core.testing.TestData
import theoneclick.client.core.testing.fakes.HttpClientEngineController
import theoneclick.client.core.testing.fakes.fakeHttpClientEngine
import theoneclick.shared.core.platform.appLogger
import theoneclick.shared.testing.dispatchers.FakeDispatchersProvider
import kotlin.test.assertEquals

class AndroidRemoteAuthenticationDataSourceTest {
    private val appLogger = appLogger()
    private val navigationController = RealNavigationController(appLogger)
    private val tokenDataSource = AndroidInMemoryTokenDataSource()
    private val httpClientEngineController = HttpClientEngineController()
    private val httpClientEngine = fakeHttpClientEngine(httpClientEngineController)
    private val logoutManager = AndroidLogoutManager(
        appLogger = appLogger,
        tokenDataSource = tokenDataSource,
        navigationController = navigationController,
    )

    private val authenticationDataSource = AndroidRemoteAuthenticationDataSource(
        httpClient = androidHttpClient(
            appLogger = appLogger,
            httpClientEngine = httpClientEngine,
            tokenDataSource = tokenDataSource,
            logoutManager = logoutManager,
        ),
        dispatchersProvider = FakeDispatchersProvider(Dispatchers.Main),
        tokenDataSource = tokenDataSource,
        appLogger = appLogger,
    )

    @Test
    fun `GIVEN user without token WHEN isUserLogged called THEN returns not logged`() {
        runTest {
            httpClientEngineController.isUserLogged = { true }

            authenticationDataSource.isUserLogged().test {
                assertEquals(UserLoggedResult.NotLogged, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN user logged WHEN isUserLogged called THEN returns logged`() {
        runTest {
            httpClientEngineController.isUserLogged = { true }
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
            httpClientEngineController.isUserLogged = { false }
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
            httpClientEngineController.isUserLogged = { true }
            httpClientEngineController.isError = { true }
            tokenDataSource.set(TestData.TOKEN)

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
            httpClientEngineController.isError = { true }

            authenticationDataSource.login(username = TestData.USERNAME, password = TestData.PASSWORD).test {
                assertEquals(RequestLoginResult.Failure, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            assertEquals(expected = null, actual = tokenDataSource.token())
        }
    }

    @Test
    fun `GIVEN valid user logged WHEN logout THEN returns logout`() {
        runTest {
            httpClientEngineController.isUserLogged = { true }
            tokenDataSource.set(TestData.TOKEN)

            authenticationDataSource.logout().test {
                assertEquals(LogoutResult.Success, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            assertEquals(expected = null, actual = tokenDataSource.token())
        }
    }

    @Test
    fun `GIVEN invalid user logged WHEN logout THEN returns failure`() {
        runTest {
            httpClientEngineController.isUserLogged = { false }

            authenticationDataSource.logout().test {
                assertEquals(LogoutResult.Failure, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN server error WHEN logout THEN returns failure`() {
        runTest {
            httpClientEngineController.isUserLogged = { true }
            httpClientEngineController.isError = { true }
            tokenDataSource.set(TestData.TOKEN)

            authenticationDataSource.logout().test {
                assertEquals(LogoutResult.Failure, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}