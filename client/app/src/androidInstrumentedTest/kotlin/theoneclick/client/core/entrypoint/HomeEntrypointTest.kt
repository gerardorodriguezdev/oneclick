package theoneclick.client.core.entrypoint

import androidx.compose.ui.test.ExperimentalTestApi
import kotlinx.coroutines.runBlocking
import org.junit.Test
import theoneclick.client.core.testing.TestData
import theoneclick.client.core.testing.base.AndroidAppIntegrationTest
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class HomeEntrypointTest : AndroidAppIntegrationTest() {

    @Test
    fun GIVEN_userNotLogged_WHEN_navigatingToDevicesList_THEN_returns() {
        httpClientEngineController.isUserLogged = { false }

        testApplication {
            homeScreenMatcher.assertScreenIsNotDisplayed()

            loginScreenMatcher.assertIsScreenDisplayed()
        }

        assertEquals(expected = null, actual = runBlocking { tokenDataSource.token() })
    }

    @Test
    fun GIVEN_userLogged_WHEN_navigatingToDevicesList_THEN_showsDevicesListScreen() {
        httpClientEngineController.isUserLogged = { true }
        runBlocking { tokenDataSource.set(TestData.TOKEN) }

        testApplication {
            homeScreenMatcher.assertScreenIsDisplayed()

            homeScreenMatcher.devicesListScreenMatcher.assertScreenIsDisplayed()
        }
    }

    @Test
    fun GIVEN_userLogged_WHEN_navigatingToAddDevice_THEN_showsAddDeviceScreen() {
        httpClientEngineController.isUserLogged = { true }
        runBlocking { tokenDataSource.set(TestData.TOKEN) }

        testApplication {
            homeScreenMatcher.navigateToAddDeviceScreen()

            homeScreenMatcher.devicesListScreenMatcher.assertScreenIsNotDisplayed()
            homeScreenMatcher.addDeviceScreenMatcher.assertScreenIsDisplayed()
        }
    }
}
