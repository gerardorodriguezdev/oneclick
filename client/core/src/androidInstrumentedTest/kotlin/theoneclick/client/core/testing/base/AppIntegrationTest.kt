package theoneclick.client.core.testing.base

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.MainTestClock
import androidx.compose.ui.test.runComposeUiTest
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import theoneclick.client.core.dataSources.AndroidInMemoryTokenDataSource
import theoneclick.client.core.entrypoint.AppEntrypoint
import theoneclick.client.core.navigation.RealNavigationController
import theoneclick.client.core.platform.AndroidAppDependencies
import theoneclick.client.core.testing.TestData
import theoneclick.client.core.testing.fakes.fakeHttpClientEngine
import theoneclick.client.core.testing.matchers.screens.AppMatcher
import theoneclick.shared.core.models.entities.Device
import theoneclick.shared.testing.dispatchers.FakeDispatchersProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class AppIntegrationTest {
    private val appEntrypoint = AppEntrypoint()

    private val dispatchersProvider = FakeDispatchersProvider(Dispatchers.Main)
    private val tokenDataSource = AndroidInMemoryTokenDataSource()
    private val navigationController = RealNavigationController()
    private var isUserLogged: Boolean = false
    private var devices: List<Device> = emptyList()
    private val httpClientEngine = fakeHttpClientEngine(
        isUserLogged = { isUserLogged },
        devices = { devices },
    )

    private val appDependencies = AndroidAppDependencies(
        httpClientEngine = httpClientEngine,
        tokenDataSource = tokenDataSource,
        dispatchersProvider = dispatchersProvider,
        navigationController = navigationController,
    )
    private val modules = appEntrypoint.buildAppModules(appDependencies)

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
        isUserLogged: Boolean = false,
        devices: List<Device> = emptyList(),
        setupBlock: ComposeUiTest.() -> Unit = {},
        block: AppMatcher.(mainClock: MainTestClock) -> Unit,
    ) {
        this.isUserLogged = isUserLogged
        this.devices = devices

        if (isUserLogged) {
            runBlocking {
                tokenDataSource.set(TestData.TOKEN)
            }
        }

        runComposeUiTest {
            setupBlock()

            setContent {
                appEntrypoint.App(navHostController = rememberNavController())
            }

            AppMatcher(this).block(mainClock)
        }
    }
}
