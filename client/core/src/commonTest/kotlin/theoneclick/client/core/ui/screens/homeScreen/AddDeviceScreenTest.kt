@file:OptIn(ExperimentalTestApi::class)

package theoneclick.client.core.ui.screens.homeScreen

import androidx.compose.ui.test.*
import theoneclick.client.core.testing.matchers.screens.homeScreen.AddDeviceScreenMatcher
import theoneclick.client.core.ui.events.homeScreen.AddDeviceEvent
import theoneclick.client.core.ui.previews.providers.screens.homeScreen.AddDeviceScreenPreviewModels
import theoneclick.client.core.ui.states.homeScreen.AddDeviceState
import theoneclick.shared.core.dataSources.models.entities.DeviceType
import kotlin.test.Test
import kotlin.test.assertEquals

class AddDeviceScreenTest {
    private val events = mutableListOf<AddDeviceEvent>()

    @Test
    fun `GIVEN init WHEN render THEN renders correctly`() {
        render(AddDeviceScreenPreviewModels.initState) {
            title.assertIsDisplayed()

            deviceNameTextField.assertIsDisplayed()

            roomNameTextField.assertIsDisplayed()

            deviceTypeMenu.container.assertIsDisplayed()
            deviceTypeMenu.container.assertIsEnabled()

            button.container.assertIsDisplayed()
            button.container.assertIsNotEnabled()

            button.progressIndicator.assertDoesNotExist()
        }
    }

    @Test
    fun `GIVEN invalid device name WHEN render THEN renders correctly`() {
        render(AddDeviceScreenPreviewModels.invalidDeviceNameState) {
            deviceNameTextField.assertTextEquals(AddDeviceScreenPreviewModels.DEVICE_NAME)
            roomNameTextField.assertTextEquals(AddDeviceScreenPreviewModels.ROOM_NAME)
            button.container.assertIsNotEnabled()
        }
    }

    @Test
    fun `GIVEN invalid room name WHEN render THEN renders correctly`() {
        render(AddDeviceScreenPreviewModels.invalidRoomNameState) {
            deviceNameTextField.assertTextEquals(AddDeviceScreenPreviewModels.DEVICE_NAME)
            roomNameTextField.assertTextEquals(AddDeviceScreenPreviewModels.ROOM_NAME)
            button.container.assertIsNotEnabled()
        }
    }

    @Test
    fun `GIVEN loading WHEN render THEN renders correctly`() {
        render(AddDeviceScreenPreviewModels.loadingState) {
            button.text.assertDoesNotExist()
            button.progressIndicator.assertIsDisplayed()
        }
    }

    @Test
    fun `GIVEN error state WHEN render called THEN renders correctly`() {
        render(
            state = AddDeviceScreenPreviewModels.errorState,
            setupBlock = { mainClock.autoAdvance = false },
        ) {
            snackbar.container.assertIsDisplayed()
        }
    }

    @Test
    fun `GIVEN valid state WHEN render THEN renders correctly`() {
        render(AddDeviceScreenPreviewModels.validState) {
            deviceNameTextField.assertTextEquals(AddDeviceScreenPreviewModels.DEVICE_NAME)
            roomNameTextField.assertTextEquals(AddDeviceScreenPreviewModels.ROOM_NAME)
            button.container.assertIsEnabled()
        }
    }

    @Test
    fun `GIVEN success state WHEN render called THEN renders correctly`() {
        render(
            state = AddDeviceScreenPreviewModels.successState,
            setupBlock = { mainClock.autoAdvance = false },
        ) {
            snackbar.container.assertIsDisplayed()
        }
    }

    @Test
    fun `GIVEN error state WHEN snackbar shown THEN sends error shown event`() {
        render(
            state = AddDeviceScreenPreviewModels.errorState,
            setupBlock = { mainClock.autoAdvance = false },
        ) { mainClock ->
            mainClock.advanceTimeBy(4_001)

            assertEquals(expected = mutableListOf<AddDeviceEvent>(AddDeviceEvent.ErrorShown), actual = events)
        }
    }

    @Test
    fun `WHEN deviceName changes THEN sends event`() {
        render(AddDeviceScreenPreviewModels.initState) {
            deviceNameTextField.performTextInput("D")

            assertEquals(
                expected = mutableListOf<AddDeviceEvent>(AddDeviceEvent.DeviceNameChanged("D")),
                actual = events,
            )
        }
    }

    @Test
    fun `WHEN roomName changes THEN sends event`() {
        render(AddDeviceScreenPreviewModels.initState) {
            roomNameTextField.performTextInput("D")

            assertEquals(
                expected = mutableListOf<AddDeviceEvent>(AddDeviceEvent.RoomNameChanged("D")),
                actual = events,
            )
        }
    }

    @Test
    fun `WHEN deviceType changes THEN sends event`() {
        render(AddDeviceScreenPreviewModels.initState) {
            deviceTypeMenu.container.performClick()
            deviceTypeMenu.dropDownMenuItem(DeviceType.BLIND).performClick()

            assertEquals(
                expected = mutableListOf<AddDeviceEvent>(AddDeviceEvent.DeviceTypeChanged(DeviceType.BLIND)),
                actual = events,
            )
        }
    }

    @Test
    fun `GIVEN valid input WHEN add device button clicked THEN sends event`() {
        render(AddDeviceScreenPreviewModels.validState) {
            button.container.performClick()

            assertEquals(
                expected = mutableListOf<AddDeviceEvent>(AddDeviceEvent.AddDeviceButtonClicked),
                actual = events,
            )
        }
    }

    @Test
    fun `GIVEN invalid input WHEN add device button clicked THEN not send event`() {
        render(AddDeviceScreenPreviewModels.invalidDeviceNameState) {
            button.container.performClick()

            assertEquals(
                expected = emptyList(),
                actual = events,
            )
        }
    }

    private fun render(
        state: AddDeviceState,
        setupBlock: ComposeUiTest.() -> Unit = {},
        block: AddDeviceScreenMatcher.(mainClock: MainTestClock) -> Unit,
    ) {
        runComposeUiTest {
            setupBlock()

            setContent {
                AddDeviceScreen(
                    state = state,
                    onEvent = { event -> events.add(event) },
                )
            }

            AddDeviceScreenMatcher(this).block(mainClock)
        }
    }
}
