package theoneclick.client.core.testing.base

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.MainTestClock
import androidx.compose.ui.test.runComposeUiTest
import androidx.navigation.compose.rememberNavController
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import theoneclick.client.core.entrypoint.AppEntrypoint
import theoneclick.client.core.testing.fakes.FakeAppDependencies
import theoneclick.client.core.testing.matchers.screens.AppMatcher
import theoneclick.shared.core.models.endpoints.Endpoint
import theoneclick.shared.core.models.responses.UserLoggedResponse
import theoneclick.shared.testing.extensions.respondJson
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class AppIntegrationTest {
    private val fakeAppDependencies = fakeAppDependencies()
    private val appEntrypoint = AppEntrypoint()
    private val modules = appEntrypoint.buildAppModules(fakeAppDependencies)

    private var isUserLogged: Boolean = false

    @BeforeTest
    fun setupKoin() {
        loadKoinModules(modules)
    }

    @AfterTest
    fun teardownKoin() {
        unloadKoinModules(modules)
    }

    @OptIn(ExperimentalTestApi::class)
    fun testApplication(
        isUserLogged: Boolean,
        setupBlock: ComposeUiTest.() -> Unit = {},
        block: AppMatcher.(mainClock: MainTestClock) -> Unit,
    ) {
        this.isUserLogged = isUserLogged

        runComposeUiTest {
            registerIdlingResource(fakeAppDependencies.idlingResource)

            setupBlock()

            setContent {
                with(appEntrypoint) {
                    App(
                        navHostController = rememberNavController(),
                    )
                }
            }

            AppMatcher(this).block(mainClock)

            unregisterIdlingResource(fakeAppDependencies.idlingResource)
        }
    }

    private fun fakeAppDependencies(): FakeAppDependencies =
        FakeAppDependencies(
            mockEngine = MockEngine { request ->
                when (request.url.fullPath) {
                    Endpoint.IS_USER_LOGGED.route -> handleIsUserLogged(isUserLogged)
                    else -> respondError(HttpStatusCode.NotFound)
                }
            },
        )

    private fun MockRequestHandleScope.handleIsUserLogged(isUserLogged: Boolean): HttpResponseData =
        if (isUserLogged) {
            respondJson<UserLoggedResponse>(UserLoggedResponse.Logged)
        } else {
            respondJson<UserLoggedResponse>(UserLoggedResponse.NotLogged)
        }
}
