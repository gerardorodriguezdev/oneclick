package theoneclick.client.core.testing.matchers.screens.homeScreen

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithTag
import theoneclick.client.core.testing.matchers.components.DefaultButtonMatcher
import theoneclick.client.core.testing.matchers.hasRole
import theoneclick.client.core.ui.screens.homeScreen.AddDeviceScreenTestTags
import theoneclick.client.core.ui.screens.homeScreen.AddDeviceScreenTestTags.dropDownItemTestTag
import theoneclick.shared.core.models.entities.DeviceType

@OptIn(ExperimentalTestApi::class)
class AddDeviceScreenMatcher(composeUiTest: ComposeUiTest) {
    val title = composeUiTest.onNodeWithTag(AddDeviceScreenTestTags.TITLE_TEST_TAG)

    val deviceNameTextField =
        composeUiTest.onNodeWithTag(AddDeviceScreenTestTags.DEVICE_NAME_TEXT_FIELD_TEST_TAG, useUnmergedTree = true)
    val roomNameTextField =
        composeUiTest.onNodeWithTag(AddDeviceScreenTestTags.ROOM_NAME_TEXT_FIELD_TEST_TAG, useUnmergedTree = true)

    val deviceTypeMenu = DeviceTypeMenuMatcher(composeUiTest)

    val button = DefaultButtonMatcher(composeUiTest)

    fun assertScreenIsDisplayed() {
        title.assertExists()
    }

    class DeviceTypeMenuMatcher(private val composeUiTest: ComposeUiTest) {
        val container = composeUiTest.onNode(hasRole(Role.DropdownList))

        fun dropDownMenuItem(deviceType: DeviceType): SemanticsNodeInteraction =
            composeUiTest.onNodeWithTag(dropDownItemTestTag(deviceType))
    }
}
