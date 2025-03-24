package theoneclick.client.core.entrypoint

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import org.junit.Test
import theoneclick.client.core.testing.base.AppIntegrationTest

@OptIn(ExperimentalTestApi::class)
class HomeEntrypointTest : AppIntegrationTest() {

    @Test
    fun GIVEN_userNotLogged_WHEN_navigatingToDevicesList_THEN_returns() {
        testApplication(
            isUserLogged = false,
            setupBlock = { mainClock.autoAdvance = false },
        ) {
            homeScreenMatcher.assertIsScreenDisplayed()

            homeScreenMatcher.devicesListScreenMatcher.devices.assertCountEquals(0)
        }
    }

    @Test
    fun GIVEN_userLogged_WHEN_navigatingToDevicesList_THEN_showsDevicesListScreen() {
        testApplication(isUserLogged = true) {
            homeScreenMatcher.assertIsScreenDisplayed()

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
