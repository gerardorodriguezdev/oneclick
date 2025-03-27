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
