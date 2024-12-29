package theoneclick.client.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import kotlinx.browser.window
import theoneclick.client.core.routes.NavigationController
import theoneclick.client.core.routes.NavigationController.NavigationEvent
import theoneclick.client.core.routes.NavigationController.NavigationEvent.*

class WasmNavigationControllerObserver(
    override val navigationController: NavigationController,
    override val navHostController: NavHostController,
) : NavigationControllerObserver {

    override fun onNavigationEvent(navigationEvent: NavigationEvent) {
        when (navigationEvent) {
            is Navigate -> {
                navHostController.navigate(route = navigationEvent.destinationRoute) {
                    navigationEvent.popUpTo?.let { popUpTo ->
                        popUpTo(popUpTo.startRoute) {
                            inclusive = popUpTo.isInclusive
                            saveState = popUpTo.saveState
                        }
                    }
                }
            }

            is PopBackStack -> navHostController.popBackStack()

            is ExternalRedirect -> window.location.href = navigationEvent.urlString
        }
    }
}

@Composable
actual fun rememberNavigationObserver(
    navigationController: NavigationController,
    navHostController: NavHostController,
): NavigationControllerObserver =
    remember(navHostController) {
        WasmNavigationControllerObserver(navigationController, navHostController)
    }
