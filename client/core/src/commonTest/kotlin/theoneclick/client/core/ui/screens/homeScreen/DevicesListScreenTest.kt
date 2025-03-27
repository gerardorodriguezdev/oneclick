@file:OptIn(ExperimentalTestApi::class)

package theoneclick.client.core.ui.screens.homeScreen

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import theoneclick.client.core.testing.matchers.screens.homeScreen.DevicesListScreenMatcher
import theoneclick.client.core.ui.events.homeScreen.DevicesListEvent
import theoneclick.client.core.ui.previews.providers.screens.homeScreen.DevicesListScreenPreviewModels.Companion.loadedState
import theoneclick.client.core.ui.states.homeScreen.DevicesListState
import theoneclick.shared.core.models.entities.Device
import kotlin.test.Test
import kotlin.test.assertEquals

class DevicesListScreenTest {
    private val events = mutableListOf<DevicesListEvent>()

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
