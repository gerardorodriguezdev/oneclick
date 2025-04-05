package theoneclick.client.core.testing.matchers.components

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import theoneclick.client.core.ui.components.DefaultButtonTestTags

@OptIn(ExperimentalTestApi::class)
class DefaultButtonMatcher(composeUiTest: ComposeUiTest) {
    val container = composeUiTest.onNodeWithTag(DefaultButtonTestTags.BUTTON_TEST_TAG, useUnmergedTree = true)
}
