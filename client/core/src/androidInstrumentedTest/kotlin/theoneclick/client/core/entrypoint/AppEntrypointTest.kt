package theoneclick.client.core.entrypoint

import androidx.compose.ui.test.ExperimentalTestApi
import kotlinx.coroutines.runBlocking
import org.junit.Test
import theoneclick.client.core.testing.TestData
import theoneclick.client.core.testing.base.AndroidAppIntegrationTest

@OptIn(ExperimentalTestApi::class)
class AppEntrypointTest : AndroidAppIntegrationTest() {

    @Test
    fun GIVEN_userNotLogged_WHEN_startApplication_THEN_showsLoadingScreen() {
        isUserLogged = false

        testApplication(setupBlock = { mainClock.autoAdvance = false }) {
            loadingScreenMatcher.assertIsScreenDisplayed()
        }
    }

    @Test
    fun GIVEN_userNotLogged_WHEN_startApplication_THEN_showsLoginScreen() {
        isUserLogged = false

        testApplication {
            loginScreenMatcher.assertIsScreenDisplayed()
        }
    }

    @Test
    fun GIVEN_userLogged_WHEN_startApplication_THEN_showsHomeScreen() {
        isUserLogged = true
        runBlocking { tokenDataSource.set(TestData.TOKEN) }

        testApplication {
            homeScreenMatcher.assertScreenIsDisplayed()
        }
    }
}
