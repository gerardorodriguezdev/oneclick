package theoneclick.client.core.viewModels

import kotlinx.coroutines.flow.flowOf
import theoneclick.client.core.models.results.RequestLoginResult
import theoneclick.client.core.navigation.NavigationController.NavigationEvent.Navigate
import theoneclick.client.core.testing.fakes.FakeAuthenticationDataSource
import theoneclick.client.core.testing.fakes.FakeNavigationController
import theoneclick.client.core.ui.events.LoginEvent.*
import theoneclick.client.core.ui.states.LoginState
import theoneclick.shared.core.models.routes.AppRoute
import theoneclick.shared.testing.dispatchers.CoroutinesTest
import theoneclick.shared.testing.extensions.assertContains
import theoneclick.shared.testing.extensions.assertIsEmpty
import kotlin.test.Test
import kotlin.test.assertEquals

class LoginViewModelTest : CoroutinesTest() {

    private val fakeNavigationController = FakeNavigationController()
    private val dataSource = FakeAuthenticationDataSource()
    private val viewModel =
        LoginViewModel(navigationController = fakeNavigationController, authenticationDataSource = dataSource)

    @Test
    fun `GIVEN initial state THEN returns initial state`() {
        assertEquals(
            expected = LoginState(),
            actual = viewModel.state.value,
        )
    }

    @Test
    fun `GIVEN valid username WHEN username changed event THEN returns updated state`() {
        viewModel.onEvent(UsernameChanged("username"))

        assertEquals(
            expected = LoginState(
                username = "username",
                isUsernameValid = true,
            ),
            actual = viewModel.state.value
        )
    }

    @Test
    fun `GIVEN invalid username WHEN username changed event THEN returns updated state`() {
        viewModel.onEvent(UsernameChanged("1"))

        assertEquals(
            expected = LoginState(
                username = "1",
                isUsernameValid = false,
            ),
            actual = viewModel.state.value
        )
    }

    @Test
    fun `GIVEN valid password WHEN password changed event THEN returns updated state`() {
        viewModel.onEvent(PasswordChanged("password12"))

        assertEquals(
            expected = LoginState(
                password = "password12",
                isPasswordValid = true,
            ),
            actual = viewModel.state.value
        )
    }

    @Test
    fun `GIVEN invalid password WHEN password changed event THEN returns updated state`() {
        viewModel.onEvent(PasswordChanged("$"))

        assertEquals(
            expected = LoginState(
                password = "$",
                isPasswordValid = false,
            ),
            actual = viewModel.state.value
        )
    }

    @Test
    fun `GIVEN valid request with local redirect WHEN register button clicked event THEN returns updated state`() {
        dataSource.requestLoginResultFlow = flowOf(RequestLoginResult.ValidLogin)
        viewModel.onEvent(UsernameChanged("username"))
        viewModel.onEvent(PasswordChanged("password12"))

        viewModel.onEvent(RegisterButtonClicked)

        assertEquals(
            expected = LoginState(
                username = "username",
                isUsernameValid = true,
                password = "password12",
                isPasswordValid = true,
                isRegisterButtonEnabled = true,
            ),
            actual = viewModel.state.value,
        )
        fakeNavigationController.events.assertContains(
            Navigate(
                destinationRoute = AppRoute.Home,
                launchSingleTop = true,
                popUpTo = Navigate.PopUpTo(
                    startRoute = AppRoute.Login,
                    isInclusive = true,
                    saveState = false,
                )
            )
        )
    }

    @Test
    fun `GIVEN invalid request WHEN register button clicked event THEN returns updated state`() {
        dataSource.requestLoginResultFlow = flowOf(RequestLoginResult.UnknownError)
        viewModel.onEvent(UsernameChanged("username"))
        viewModel.onEvent(PasswordChanged("password12"))

        viewModel.onEvent(RegisterButtonClicked)

        assertEquals(
            expected = LoginState(
                username = "username",
                isUsernameValid = true,
                password = "password12",
                isPasswordValid = true,
                isRegisterButtonEnabled = true,
                showError = true,
            ),
            actual = viewModel.state.value,
        )
        fakeNavigationController.events.assertIsEmpty()
    }

    @Test
    fun `WHEN error shown event THEN returns updated state`() {
        dataSource.requestLoginResultFlow = flowOf(RequestLoginResult.UnknownError)
        viewModel.onEvent(UsernameChanged("username"))
        viewModel.onEvent(PasswordChanged("password12"))
        viewModel.onEvent(RegisterButtonClicked)
        viewModel.onEvent(ErrorShown)

        assertEquals(
            expected = LoginState(
                username = "username",
                isUsernameValid = true,
                password = "password12",
                isPasswordValid = true,
                isRegisterButtonEnabled = true,
                showError = false,
            ),
            actual = viewModel.state.value,
        )
        fakeNavigationController.events.assertIsEmpty()
    }
}
