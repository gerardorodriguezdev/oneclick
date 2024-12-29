package theoneclick.client.core.testing.matchers.screens.homeScreen

import androidx.compose.ui.test.*
import theoneclick.client.core.testing.matchers.components.DefaultSnackbarMatcher
import theoneclick.client.core.testing.matchers.onNodeWithTag
import theoneclick.client.core.ui.screens.homeScreen.DevicesListScreenTestTags

@OptIn(ExperimentalTestApi::class)
class DevicesListScreenMatcher(composeUiTest: ComposeUiTest) {
    val listContainer = composeUiTest.onNodeWithTag(DevicesListScreenTestTags.LIST_CONTAINER)

    val devices = composeUiTest.onAllNodesWithTag(DevicesListScreenTestTags.DEVICE_CONTAINER)
    val snackbar = DefaultSnackbarMatcher(composeUiTest)

    fun SemanticsNodeInteractionCollection.device(index: Int): DeviceMatcher =
        DeviceMatcher(container = get(index).onChildren())

    fun assertScreenIsDisplayed() {
        listContainer.assertIsDisplayed()
    }

    fun assertScreenIsNotDisplayed() {
        listContainer.assertDoesNotExist()
    }

    class DeviceMatcher(
        private val container: SemanticsNodeInteractionCollection,
    ) {
        val deviceNameText = container.onNodeWithTag(DevicesListScreenTestTags.DEVICE_NAME_TEXT)
        val roomNameText = container.onNodeWithTag(DevicesListScreenTestTags.ROOM_NAME_TEXT)
        val openedStateSwitch = container.onNodeWithTag(DevicesListScreenTestTags.OPENING_STATE_SWITCH)
        val rotationSlider = container.onNodeWithTag(DevicesListScreenTestTags.ROTATION_SLIDER)

        fun label(label: String) = container.onNodeWithTag(DevicesListScreenTestTags.labelTestTag(label))
    }
}
