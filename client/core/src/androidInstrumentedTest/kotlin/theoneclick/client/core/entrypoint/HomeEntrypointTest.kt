package theoneclick.client.core.entrypoint

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import org.junit.Test
import theoneclick.client.core.testing.base.HomeIntegrationTest

@OptIn(ExperimentalTestApi::class)
class HomeEntrypointTest : HomeIntegrationTest() {

    @Test
    fun GIVEN_userNotLogged_WHEN_navigatingToDevicesList_THEN_returns() {
        testApplication(
            isUserLogged = false,
            setupBlock = { mainClock.autoAdvance = false },
        ) {
            assertIsScreenDisplayed()

            devicesListScreenMatcher.devices.assertCountEquals(0)
        }
    }

    @Test
    fun GIVEN_userLogged_WHEN_navigatingToDevicesList_THEN_showsDevicesListScreen() {
        testApplication(isUserLogged = true) {
            assertIsScreenDisplayed()

            devicesListScreenMatcher.assertScreenIsDisplayed()
        }
    }

    @Test
    fun GIVEN_userLogged_WHEN_navigatingToAddDevice_THEN_showsAddDeviceScreen() {
        testApplication(isUserLogged = true) {
            navigateToAddDeviceScreen()

            devicesListScreenMatcher.assertScreenIsNotDisplayed()
            addDeviceScreenMatcher.assertScreenIsDisplayed()
        }
    }
}
