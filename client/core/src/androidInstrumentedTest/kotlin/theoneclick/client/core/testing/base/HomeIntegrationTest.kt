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
import org.koin.core.module.Module
import theoneclick.client.core.entrypoint.HomeEntrypoint
import theoneclick.client.core.entrypoint.buildCoreModule
import theoneclick.client.core.platform.AndroidAppDependencies
import theoneclick.client.core.testing.idlingResources.TestIdlingResource
import theoneclick.client.core.testing.matchers.screens.homeScreen.HomeScreenMatcher
import theoneclick.client.core.ui.previews.providers.screens.homeScreen.DevicesListScreenPreviewModels
import theoneclick.shared.core.models.endpoints.ClientEndpoint
import theoneclick.shared.core.models.responses.DevicesResponse
import theoneclick.shared.testing.extensions.respondJson
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class HomeIntegrationTest {
    private val idlingResource = TestIdlingResource()
    private val appDependencies = AndroidAppDependencies(
        httpClientEngine = MockEngine { request ->
            when (request.url.fullPath) {
                ClientEndpoint.DEVICES.route -> handleDevices(isUserLogged)
                else -> respondError(HttpStatusCode.NotFound)
            }
        },
        idlingResource = idlingResource,
    )
    private val homeEntrypoint = HomeEntrypoint()
    private val modules: List<Module> = buildModules()

    private var isUserLogged: Boolean = false

    @BeforeTest
    fun setUp() {
        loadKoinModules(modules)
    }

    @AfterTest
    fun tearDown() {
        unloadKoinModules(modules)
    }

    private fun buildModules(): List<Module> {
        val coreModule = buildCoreModule(appDependencies)
        val loggedModule = homeEntrypoint.buildLoggedModule(coreModule)
        return listOf(coreModule, loggedModule)
    }

    @OptIn(ExperimentalTestApi::class)
    fun testApplication(
        isUserLogged: Boolean,
        setupBlock: ComposeUiTest.() -> Unit = {},
        block: HomeScreenMatcher.(mainClock: MainTestClock) -> Unit,
    ) {
        this.isUserLogged = isUserLogged

        runComposeUiTest {
            registerIdlingResource(idlingResource)

            setupBlock()

            setContent {
                with(homeEntrypoint) {
                    HomeScreen(
                        navHostController = rememberNavController(),
                    )
                }
            }

            HomeScreenMatcher(this).block(mainClock)

            unregisterIdlingResource(idlingResource)
        }
    }

    private fun MockRequestHandleScope.handleDevices(isUserLogged: Boolean): HttpResponseData =
        if (isUserLogged) {
            respondJson<DevicesResponse>(DevicesResponse(devices = DevicesListScreenPreviewModels.devices))
        } else {
            respondError(HttpStatusCode.Unauthorized)
        }
}
