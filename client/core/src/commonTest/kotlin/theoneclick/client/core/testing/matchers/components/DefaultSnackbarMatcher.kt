package theoneclick.client.core.testing.matchers.components

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import theoneclick.client.core.ui.components.DefaultSnackbarTestTags

@OptIn(ExperimentalTestApi::class)
class DefaultSnackbarMatcher(composeUiTest: ComposeUiTest) {
    val container = composeUiTest.onNodeWithTag(DefaultSnackbarTestTags.SNACKBAR_TEST_TAG, useUnmergedTree = true)
}
