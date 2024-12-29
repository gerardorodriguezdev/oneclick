package theoneclick.client.core.testing.matchers.screens

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import theoneclick.client.core.testing.matchers.screens.homeScreen.HomeScreenMatcher

@OptIn(ExperimentalTestApi::class)
class AppMatcher(composeUiTest: ComposeUiTest) {
    val loadingScreenMatcher = LoadingScreenMatcher(composeUiTest)
    val loginScreenMatcher = LoginScreenMatcher(composeUiTest)
    val homeScreenMatcher = HomeScreenMatcher(composeUiTest)
}
