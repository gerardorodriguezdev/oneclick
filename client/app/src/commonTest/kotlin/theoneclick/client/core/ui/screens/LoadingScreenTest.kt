package theoneclick.client.core.ui.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import theoneclick.client.core.testing.matchers.screens.LoadingScreenMatcher

class LoadingScreenTest {

    @OptIn(ExperimentalTestApi::class)
    private fun render(block: LoadingScreenMatcher.() -> Unit) {
        runComposeUiTest {
            setContent {
                LoadingScreen()
            }

            LoadingScreenMatcher(this).apply(block)
        }
    }
}
