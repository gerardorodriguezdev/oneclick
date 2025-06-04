package theoneclick.client.app.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import theoneclick.client.app.navigation.NavigationController
import theoneclick.client.app.navigation.NavigationController.NavigationEvent
import theoneclick.client.app.navigation.NavigationControllerObserver
import theoneclick.client.app.navigation.rememberNavigationObserver
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
