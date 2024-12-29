package theoneclick.client.core.testing.matchers.components

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import theoneclick.client.core.ui.components.DefaultButtonTestTags

@OptIn(ExperimentalTestApi::class)
class DefaultButtonMatcher(composeUiTest: ComposeUiTest) {
    val container = composeUiTest.onNodeWithTag(DefaultButtonTestTags.BUTTON_TEST_TAG, useUnmergedTree = true)
    val text = composeUiTest.onNodeWithTag(DefaultButtonTestTags.BUTTON_TEXT_TEST_TAG, useUnmergedTree = true)
    val progressIndicator =
        composeUiTest.onNodeWithTag(DefaultButtonTestTags.BUTTON_PROGRESS_INDICATOR_TEST_TAG, useUnmergedTree = true)
}
