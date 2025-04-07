package theoneclick.client.core.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import theoneclick.client.core.navigation.NavigationController
import theoneclick.client.core.navigation.NavigationController.NavigationEvent
import theoneclick.client.core.navigation.NavigationControllerObserver
import theoneclick.client.core.navigation.rememberNavigationObserver
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
