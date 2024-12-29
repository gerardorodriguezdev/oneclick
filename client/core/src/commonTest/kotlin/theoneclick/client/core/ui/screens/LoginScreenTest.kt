package theoneclick.client.core.ui.screens

import androidx.compose.ui.test.*
import theoneclick.client.core.testing.matchers.screens.LoginScreenMatcher
import theoneclick.client.core.ui.events.LoginEvent
import theoneclick.client.core.ui.previews.providers.screens.LoginScreenPreviewModels
import theoneclick.client.core.ui.states.LoginState
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class LoginScreenTest {
    private val events = mutableListOf<LoginEvent>()

    @Test
    fun `GIVEN initial state WHEN render called THEN renders correctly`() {
        render(LoginScreenPreviewModels.initialState) {
            title.assertIsDisplayed()

            usernameTextField.assertIsDisplayed()

            passwordTextField.assertIsDisplayed()

            button.container.assertIsDisplayed()
            button.container.assertIsNotEnabled()

            button.progressIndicator.assertDoesNotExist()
        }
    }

    @Test
    fun `GIVEN invalid username state WHEN render called THEN renders correctly`() {
        render(LoginScreenPreviewModels.invalidUsernameState) {
            usernameTextField.assertTextEquals("Username")
            passwordTextField.assertTextEquals("••••••••")
            button.container.assertIsNotEnabled()
        }
    }

    @Test
    fun `GIVEN invalid password state WHEN render called THEN renders correctly`() {
        render(LoginScreenPreviewModels.invalidPasswordState) {
            usernameTextField.assertTextEquals("Username")
            passwordTextField.assertTextEquals("••••••••")
            button.container.assertIsNotEnabled()
        }
    }

    @Test
    fun `GIVEN valid state WHEN render called THEN renders correctly`() {
        render(LoginScreenPreviewModels.validState) {
            usernameTextField.assertTextEquals("Username")
            passwordTextField.assertTextEquals("••••••••")
            button.container.assertIsEnabled()
        }
    }

    @Test
    fun `WHEN username changes THEN sends event`() {
        render(LoginScreenPreviewModels.initialState) {
            usernameTextField.performTextInput("U")

            assertEquals(
                expected = mutableListOf<LoginEvent>(LoginEvent.UsernameChanged("U")),
                actual = events
            )
        }
    }

    @Test
    fun `WHEN password changes THEN sends event`() {
        render(LoginScreenPreviewModels.initialState) {
            passwordTextField.performTextInput("P")

            assertEquals(
                expected = mutableListOf<LoginEvent>(LoginEvent.PasswordChanged("P")),
                actual = events
            )
        }
    }

    @Test
    fun `GIVEN invalid input WHEN register button clicked THEN event not sent`() {
        render(LoginScreenPreviewModels.invalidUsernameState) {
            button.container.performClick()

            assertEquals(expected = emptyList(), actual = events)
        }
    }

    @Test
    fun `GIVEN valid input WHEN register button clicked THEN sends event`() {
        render(LoginScreenPreviewModels.validState) {
            button.container.performClick()

            assertEquals(expected = mutableListOf<LoginEvent>(LoginEvent.RegisterButtonClicked), actual = events)
        }
    }

    @Test
    fun `GIVEN error state WHEN render called THEN renders correctly`() {
        render(
            state = LoginScreenPreviewModels.errorState,
            setupBlock = { mainClock.autoAdvance = false },
        ) {
            snackbar.container.assertIsDisplayed()
        }
    }

    @Test
    fun `GIVEN loading state WHEN render called THEN renders correctly`() {
        render(LoginScreenPreviewModels.loadingState) {
            button.text.assertDoesNotExist()
            button.progressIndicator.assertIsDisplayed()
        }
    }

    @Test
    fun `GIVEN error state WHEN snackbar shown THEN sends error shown event`() {
        render(
            state = LoginScreenPreviewModels.errorState,
            setupBlock = { mainClock.autoAdvance = false },
        ) { mainClock ->
            mainClock.advanceTimeBy(4_001)

            assertEquals(expected = mutableListOf<LoginEvent>(LoginEvent.ErrorShown), actual = events)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    private fun render(
        state: LoginState,
        setupBlock: ComposeUiTest.() -> Unit = {},
        block: LoginScreenMatcher.(mainClock: MainTestClock) -> Unit,
    ) {
        runComposeUiTest {
            setupBlock()

            setContent {
                LoginScreen(
                    state = state,
                    onEvent = { event -> events.add(event) },
                )
            }

            LoginScreenMatcher(this).block(mainClock)
        }
    }
}
