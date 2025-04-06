package theoneclick.client.core.testing.base

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.MainTestClock
import androidx.compose.ui.test.runComposeUiTest
import androidx.navigation.compose.rememberNavController
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import theoneclick.client.core.entrypoint.AppEntrypoint
import theoneclick.client.core.platform.AppDependencies
import theoneclick.client.core.testing.matchers.screens.AppMatcher
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class AppIntegrationTest {
    protected abstract val appDependencies: AppDependencies
    private val appEntrypoint by lazy {
        AppEntrypoint(appDependencies = appDependencies, startKoin = false)
    }

    @BeforeTest
    fun setupKoin() {
        loadKoinModules(appEntrypoint.koinModules)
    }

    @AfterTest
    fun teardownKoin() {
        unloadKoinModules(appEntrypoint.koinModules)
    }

    @OptIn(ExperimentalTestApi::class)
    fun testApplication(
        setupBlock: ComposeUiTest.() -> Unit = {},
        block: AppMatcher.(mainClock: MainTestClock) -> Unit,
    ) {
        runComposeUiTest {
            setupBlock()

            setContent {
                appEntrypoint.App(navHostController = rememberNavController())
            }

            AppMatcher(this).block(mainClock)
        }
    }
}
