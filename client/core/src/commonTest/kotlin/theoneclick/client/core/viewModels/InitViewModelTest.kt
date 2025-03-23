package theoneclick.client.core.viewModels

import kotlinx.coroutines.flow.flowOf
import theoneclick.client.core.extensions.popUpToInclusive
import theoneclick.client.core.navigation.NavigationController.NavigationEvent
import theoneclick.client.core.testing.fakes.FakeAuthenticationDataSource
import theoneclick.client.core.testing.fakes.FakeNavigationController
import theoneclick.client.core.models.results.UserLoggedResult
import theoneclick.shared.core.models.routes.AppRoute.*
import theoneclick.shared.core.models.routes.base.Route
import theoneclick.shared.testing.dispatchers.CoroutinesTest
import theoneclick.shared.testing.extensions.assertContains
import kotlin.test.Test

class InitViewModelTest : CoroutinesTest() {
    private val navigationController = FakeNavigationController()
    private val dataSource = FakeAuthenticationDataSource()

    @Test
    fun `GIVEN user logged WHEN init THEN goes to starting route`() {
        val startingRoute = Home
        dataSource.userLoggedResult = flowOf(UserLoggedResult.Logged)

        initViewModel()

        navigationController.events.assertContains(
            expectedNavigationEvents(startingRoute = startingRoute)
        )
    }

    @Test
    fun `GIVEN not logged WHEN init THEN goes to login route`() {
        dataSource.userLoggedResult = flowOf(UserLoggedResult.NotLogged)

        initViewModel()

        navigationController.events.assertContains(
            expectedNavigationEvents(Login)
        )
    }

    @Test
    fun `GIVEN data source error WHEN init THEN goes to login route`() {
        dataSource.userLoggedResult = flowOf(UserLoggedResult.UnknownError)

        initViewModel()

        navigationController.events.assertContains(
            expectedNavigationEvents(Login)
        )
    }

    private fun expectedNavigationEvents(startingRoute: Route): NavigationEvent =
        NavigationEvent.Navigate(
            destinationRoute = startingRoute,
            popUpTo = popUpToInclusive(startRoute = Init),
            launchSingleTop = true,
        )

    private fun initViewModel(): InitViewModel =
        InitViewModel(
            authenticationDataSource = dataSource,
            navigationController = navigationController,
        )
}
