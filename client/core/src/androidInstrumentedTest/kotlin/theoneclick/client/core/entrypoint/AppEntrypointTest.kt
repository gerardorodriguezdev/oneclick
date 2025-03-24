package theoneclick.client.core.entrypoint

import androidx.compose.ui.test.ExperimentalTestApi
import org.junit.Test
import theoneclick.client.core.testing.base.AppIntegrationTest

@OptIn(ExperimentalTestApi::class)
class AppEntrypointTest : AppIntegrationTest() {

    @Test
    fun GIVEN_userNotLogged_WHEN_startApplication_THEN_showsLoadingScreen() {
        testApplication(
            isUserLogged = false,
            setupBlock = { mainClock.autoAdvance = false },
        ) {
            loadingScreenMatcher.assertIsScreenDisplayed()
        }
    }

    @Test
    fun GIVEN_userNotLogged_WHEN_startApplication_THEN_showsLoginScreen() {
        testApplication(isUserLogged = false) {
            loginScreenMatcher.assertIsScreenDisplayed()
        }
    }

    @Test
    fun GIVEN_userLogged_WHEN_startApplication_THEN_showsHomeScreen() {
        testApplication(isUserLogged = true) {
            homeScreenMatcher.assertScreenIsDisplayed()
        }
    }
}
