package theoneclick.client.core.platform

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import theoneclick.client.core.models.results.LogoutResult
import theoneclick.client.core.models.results.RequestLoginResult
import theoneclick.client.core.models.results.UserLoggedResult
import theoneclick.client.core.navigation.DefaultNavigationController
import theoneclick.client.core.testing.TestData
import theoneclick.client.core.testing.fakes.HttpClientEngineController
import theoneclick.client.core.testing.fakes.fakeHttpClientEngine
import theoneclick.shared.core.platform.appLogger
import theoneclick.shared.testing.dispatchers.FakeDispatchersProvider
import kotlin.test.Test
import kotlin.test.assertEquals

class WasmRemoteAuthenticationDataSourceTest {
    private val appLogger = appLogger()
    private val navigationController = DefaultNavigationController(appLogger())
    private val httpClientEngineController = HttpClientEngineController()
    private val logoutManager = WasmLogoutManager(navigationController)
    private val httpClientEngine = fakeHttpClientEngine(httpClientEngineController)

    private val authenticationDataSource = WasmRemoteAuthenticationDataSource(
        httpClient = wasmHttpClient(
            httpClientEngine = httpClientEngine,
            logoutManager = logoutManager,
        ),
        dispatchersProvider = FakeDispatchersProvider(Dispatchers.Unconfined),
        appLogger = appLogger,
    )

    @Test
    fun `GIVEN user logged WHEN isUserLogged called THEN returns logged`() {
        runTest {
            httpClientEngineController.isUserLogged = { true }

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

            authenticationDataSource.isUserLogged().test {
                assertEquals(UserLoggedResult.UnknownError, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `GIVEN valid data WHEN login THEN returns valid login`() {
        runTest {
            authenticationDataSource.login(username = TestData.USERNAME, password = TestData.PASSWORD).test {
                assertEquals(RequestLoginResult.ValidLogin, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
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
        }
    }

    @Test
    fun `GIVEN valid user logged WHEN logout THEN returns logout`() {
        runTest {
            httpClientEngineController.isUserLogged = { true }

            authenticationDataSource.logout().test {
                assertEquals(LogoutResult.Success, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
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

            authenticationDataSource.logout().test {
                assertEquals(LogoutResult.Failure, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}
