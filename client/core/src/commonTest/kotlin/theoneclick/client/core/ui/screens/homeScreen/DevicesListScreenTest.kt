@file:OptIn(ExperimentalTestApi::class)

package theoneclick.client.core.ui.screens.homeScreen

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import theoneclick.client.core.testing.matchers.assertProgressBarRangeInfo
import theoneclick.client.core.testing.matchers.screens.homeScreen.DevicesListScreenMatcher
import theoneclick.client.core.ui.events.homeScreen.DevicesListEvent
import theoneclick.client.core.ui.previews.providers.screens.homeScreen.DevicesListScreenPreviewModels
import theoneclick.client.core.ui.previews.providers.screens.homeScreen.DevicesListScreenPreviewModels.Companion.loadedState
import theoneclick.client.core.ui.states.homeScreen.DevicesListState
import theoneclick.shared.core.models.entities.Device
import kotlin.test.Test
import kotlin.test.assertEquals

class DevicesListScreenTest {
    private val events = mutableListOf<DevicesListEvent>()

    @Test
    fun `GIVEN init WHEN render THEN renders correctly`() {
        render(DevicesListScreenPreviewModels.initState) {
            devices.assertCountEquals(0)
        }
    }

    @Test
    fun `GIVEN loading WHEN render THEN renders correctly`() {
        render(DevicesListScreenPreviewModels.loadingState) {
            devices.assertCountEquals(0)
        }
    }

    @Test
    fun `GIVEN error WHEN render THEN renders correctly`() {
        render(
            state = DevicesListScreenPreviewModels.isErrorState,
            setupBlock = { mainClock.autoAdvance = false },
        ) {
            snackbar.container.assertIsDisplayed()
        }
    }

    @Test
    fun `GIVEN loaded WHEN render THEN renders correctly`() {
        render(loadedState) {
            devices.assertCountEquals(3)

            devices.device(0).assertDevice(isOpened = false, progress = 0f)
            devices.device(1).assertDevice(isOpened = true, progress = 81.818184f)
            devices.device(2).assertDevice(isOpened = false, progress = 180f)
        }
    }

    @Test
    fun `GIVEN loaded WHEN open blind THEN sends update event`() {
        render(loadedState) {
            devices.device(0).openedStateSwitch.performTouchInput { swipeRight() }
        }

        assertEquals(
            expected = listOf<DevicesListEvent>(
                DevicesListEvent.UpdateDevice(
                    updatedDevice = (loadedState.devices[0] as Device.Blind).toggle(true),
                )
            ),
            actual = events,
        )
    }

    @Test
    fun `GIVEN loaded WHEN rotate blind THEN sends update event`() {
        render(loadedState) {
            devices.device(0).rotationSlider.performSemanticsAction(SemanticsActions.SetProgress) { it(81.818184f) }
        }

        assertEquals(
            expected = listOf<DevicesListEvent>(
                DevicesListEvent.UpdateDevice(
                    updatedDevice = (loadedState.devices[0] as Device.Blind).rotate(81),
                )
            ),
            actual = events,
        )
    }

    @Test
    fun `GIVEN isError WHEN error shown THEN sends error shown event`() {
        render(
            state = DevicesListScreenPreviewModels.isErrorState,
            setupBlock = { mainClock.autoAdvance = false },
            block = { mainClock ->
                mainClock.advanceTimeBy(4_001)

                assertEquals(
                    expected = listOf<DevicesListEvent>(DevicesListEvent.ErrorShown),
                    actual = events,
                )
            }
        )
    }

    private fun DevicesListScreenMatcher.DeviceMatcher.assertDevice(
        isOpened: Boolean,
        progress: Float,
    ) {
        deviceNameText.assertTextEquals(DevicesListScreenPreviewModels.DEVICE_NAME)

        roomNameText.assertTextEquals(DevicesListScreenPreviewModels.ROOM_NAME)

        if (isOpened) {
            openedStateSwitch.assertIsOn()
        } else {
            openedStateSwitch.assertIsOff()
        }

        rotationSlider.assertProgressBarRangeInfo(progressBarRangeInfo(progress))
    }

    private fun progressBarRangeInfo(progress: Float): ProgressBarRangeInfo =
        ProgressBarRangeInfo(progress, Device.Blind.blindRange.toClosedFloatingPointRange(), 10)

    @OptIn(ExperimentalTestApi::class)
    private fun render(
        state: DevicesListState,
        setupBlock: ComposeUiTest.() -> Unit = {},
        block: DevicesListScreenMatcher.(mainClock: MainTestClock) -> Unit,
    ) {
        runComposeUiTest {
            setupBlock()

            setContent {
                DevicesListScreen(
                    state = state,
                    onEvent = { event -> events.add(event) },
                )
            }

            DevicesListScreenMatcher(this).block(mainClock)
        }
    }
}
