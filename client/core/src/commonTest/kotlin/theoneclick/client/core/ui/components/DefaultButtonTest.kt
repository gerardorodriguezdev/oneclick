@file:OptIn(ExperimentalTestApi::class)

package theoneclick.client.core.ui.components

import androidx.compose.ui.test.*
import theoneclick.client.core.testing.matchers.components.DefaultButtonMatcher
import theoneclick.client.core.ui.previews.providers.components.DefaultButtonPreviewModels
import theoneclick.client.core.ui.previews.providers.components.DefaultButtonPreviewModels.DefaultButtonModel
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultButtonTest {
    private var clickEvents = 0

    @Test
    fun `GIVEN button enabled WHEN render THEN renders correctly`() {
        render(DefaultButtonPreviewModels.enabledButtonModel) {
            container.assertIsDisplayed()
            container.assertIsEnabled()
            text.assertTextEquals(DefaultButtonPreviewModels.BUTTON_TEXT)
            progressIndicator.assertDoesNotExist()
        }
    }

    @Test
    fun `GIVEN button disabled WHEN render THEN renders correctly`() {
        render(DefaultButtonPreviewModels.disabledButtonModel) {
            container.assertIsDisplayed()
            container.assertIsNotEnabled()
            text.assertTextEquals(DefaultButtonPreviewModels.BUTTON_TEXT)
            progressIndicator.assertDoesNotExist()
        }
    }

    @Test
    fun `GIVEN button loading WHEN render THEN renders correctly`() {
        render(DefaultButtonPreviewModels.loadingButtonModel) {
            container.assertIsDisplayed()
            container.assertIsNotEnabled()
            text.assertDoesNotExist()
            progressIndicator.assertIsDisplayed()
        }
    }

    @Test
    fun `GIVEN button enabled WHEN click THEN sends click event`() {
        render(DefaultButtonPreviewModels.enabledButtonModel) {
            container.performClick()
        }

        assertEquals(1, clickEvents)
    }

    @Test
    fun `GIVEN button disabled WHEN click THEN not send click event`() {
        render(DefaultButtonPreviewModels.disabledButtonModel) {
            container.performClick()
        }

        assertEquals(0, clickEvents)
    }

    @Test
    fun `GIVEN button loading WHEN click THEN not send click event`() {
        render(DefaultButtonPreviewModels.loadingButtonModel) {
            container.performClick()
        }

        assertEquals(0, clickEvents)
    }

    @OptIn(ExperimentalTestApi::class)
    private fun render(defaultButtonModel: DefaultButtonModel, block: DefaultButtonMatcher.() -> Unit) {
        runComposeUiTest {
            setContent {
                DefaultButton(
                    text = defaultButtonModel.text,
                    onClick = { clickEvents++ },
                    isEnabled = defaultButtonModel.isEnabled,
                    isLoading = defaultButtonModel.isLoading,
                )
            }

            DefaultButtonMatcher(this).block()
        }
    }
}
