package theoneclick.client.core.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import theoneclick.client.core.platform.NavigationControllerObserver
import theoneclick.client.core.platform.rememberNavigationObserver
import theoneclick.client.core.routes.NavigationController
import theoneclick.client.core.routes.NavigationController.NavigationEvent
import theoneclick.shared.core.models.routes.base.Route

fun popUpToInclusive(
    startRoute: Route,
    saveState: Boolean = false,
): NavigationEvent.Navigate.PopUpTo =
    NavigationEvent.Navigate.PopUpTo(
        startRoute = startRoute,
        isInclusive = true,
        saveState = saveState,
    )

@Composable
fun RegisterNavigationControllerObserver(
    navigationController: NavigationController,
    navHostController: NavHostController
) {
    val navigationControllerObserver: NavigationControllerObserver =
        rememberNavigationObserver(navigationController, navHostController)

    LaunchedEffect(navHostController, navigationController, navigationControllerObserver) {
        navigationControllerObserver.subscribe()
    }
}
