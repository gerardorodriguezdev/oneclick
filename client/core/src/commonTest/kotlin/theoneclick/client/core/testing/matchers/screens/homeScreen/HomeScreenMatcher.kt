package theoneclick.client.core.testing.matchers.screens.homeScreen

import androidx.compose.ui.test.*
import theoneclick.client.core.ui.screens.homeScreen.HomeScreenScaffoldTestTags.navigationItemTestTag
import theoneclick.shared.core.models.routes.HomeRoute

@OptIn(ExperimentalTestApi::class)
class HomeScreenMatcher(composeUiTest: ComposeUiTest) {
    val devicesListNavigationItem = composeUiTest.onNodeWithTag(navigationItemTestTag(HomeRoute.DevicesList))
    val addDeviceNavigationItem = composeUiTest.onNodeWithTag(navigationItemTestTag(HomeRoute.AddDevice))

    val devicesListScreenMatcher = DevicesListScreenMatcher(composeUiTest)
    val addDeviceScreenMatcher = AddDeviceScreenMatcher(composeUiTest)

    fun navigateToDevicesListScreen() {
        devicesListNavigationItem.performClick()
    }

    fun navigateToAddDeviceScreen() {
        addDeviceNavigationItem.performClick()
    }

    fun assertScreenIsDisplayed() {
        devicesListNavigationItem.assertIsDisplayed()
        addDeviceNavigationItem.assertIsEnabled()
    }

    fun assertScreenIsNotDisplayed() {
        devicesListNavigationItem.assertDoesNotExist()
        addDeviceNavigationItem.assertDoesNotExist()
    }
}
