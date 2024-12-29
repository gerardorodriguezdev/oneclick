package theoneclick.client.core.testing.matchers.screens

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import theoneclick.client.core.testing.matchers.components.DefaultButtonMatcher
import theoneclick.client.core.testing.matchers.components.DefaultSnackbarMatcher
import theoneclick.client.core.ui.screens.LoginScreenTestTags

@OptIn(ExperimentalTestApi::class)
class LoginScreenMatcher(
    composeUiTest: ComposeUiTest
) {
    val title = composeUiTest.onNodeWithTag(LoginScreenTestTags.TITLE_TEST_TAG)
    val usernameTextField =
        composeUiTest.onNodeWithTag(LoginScreenTestTags.USERNAME_TEXT_FIELD_TEST_TAG, useUnmergedTree = true)
    val passwordTextField =
        composeUiTest.onNodeWithTag(LoginScreenTestTags.PASSWORD_TEXT_FIELD_TEST_TAG, useUnmergedTree = true)
    val snackbar = DefaultSnackbarMatcher(composeUiTest)
    val button = DefaultButtonMatcher(composeUiTest)

    fun assertIsScreenDisplayed() {
        title.assertIsDisplayed()
    }
}
