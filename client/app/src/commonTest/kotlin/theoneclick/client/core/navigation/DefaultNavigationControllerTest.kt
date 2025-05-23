package theoneclick.client.core.navigation

import app.cash.turbine.turbineScope
import kotlinx.coroutines.test.runTest
import theoneclick.shared.core.models.routes.AppRoute
import theoneclick.shared.core.platform.appLogger
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultNavigationControllerTest {

    private val navigationController = DefaultNavigationController(appLogger())

    @Test
    fun `GIVEN no events WHEN sendNavigationEvent THEN sends navigation event`() =
        runTest {
            turbineScope {
                val testObserver = navigationController.navigationEvents.testIn(backgroundScope)

                navigationController.sendNavigationEvent(NavigationController.NavigationEvent.Navigate(AppRoute.Home))

                assertEquals(NavigationController.NavigationEvent.Navigate(AppRoute.Home), testObserver.awaitItem())
            }
        }
}
