package theoneclick.client.core.testing.matchers.screens

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import theoneclick.client.core.ui.screens.LoadingScreenTestTags

@OptIn(ExperimentalTestApi::class)
class LoadingScreenMatcher(composeUiTest: ComposeUiTest) {
    val progressIndicator = composeUiTest.onNodeWithTag(LoadingScreenTestTags.PROGRESS_INDICATOR_TEST_TAG)

    fun assertIsScreenDisplayed() {
        progressIndicator.assertIsDisplayed()
    }
}
