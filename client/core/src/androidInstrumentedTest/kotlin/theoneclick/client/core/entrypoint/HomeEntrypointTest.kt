package theoneclick.client.core.entrypoint

import androidx.compose.ui.test.ExperimentalTestApi
import org.junit.Test
import theoneclick.client.core.testing.base.AppIntegrationTest

@OptIn(ExperimentalTestApi::class)
class HomeEntrypointTest : AppIntegrationTest() {

    @Test
    fun GIVEN_userNotLogged_WHEN_navigatingToDevicesList_THEN_returns() {
        testApplication(isUserLogged = false) {
            homeScreenMatcher.assertScreenIsNotDisplayed()

            loginScreenMatcher.assertIsScreenDisplayed()
        }
    }

    @Test
    fun GIVEN_userLogged_WHEN_navigatingToDevicesList_THEN_showsDevicesListScreen() {
        testApplication(isUserLogged = true) {
            homeScreenMatcher.assertScreenIsDisplayed()

            homeScreenMatcher.devicesListScreenMatcher.assertScreenIsDisplayed()
        }
    }

    @Test
    fun GIVEN_userLogged_WHEN_navigatingToAddDevice_THEN_showsAddDeviceScreen() {
        testApplication(isUserLogged = true) {
            homeScreenMatcher.navigateToAddDeviceScreen()

            homeScreenMatcher.devicesListScreenMatcher.assertScreenIsNotDisplayed()
            homeScreenMatcher.addDeviceScreenMatcher.assertScreenIsDisplayed()
        }
    }
}
