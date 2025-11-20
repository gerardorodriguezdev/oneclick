package oneclick.client.apps.user.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import oneclick.client.apps.user.navigation.NavigationController.NavigationEvent.Navigate
import oneclick.client.apps.user.navigation.models.routes.AppRoute
import oneclick.client.apps.user.navigation.models.routes.base.Route

fun popUpToInclusive(
    startRoute: Route,
    saveState: Boolean = false,
): Navigate.PopUpTo =
    Navigate.PopUpTo(
        startRoute = startRoute,
        isInclusive = true,
        saveState = saveState,
    )

suspend fun NavigationController.logout() {
    sendNavigationEvent(
        Navigate(
            destinationRoute = AppRoute.Login,
            launchSingleTop = true,
            popUpTo = popUpToInclusive(startRoute = AppRoute.Home)
        )
    )
}

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

@Composable
private fun rememberNavigationObserver(
    navigationController: NavigationController,
    navHostController: NavHostController,
): NavigationControllerObserver =
    remember {
        DefaultNavigationControllerObserver(navigationController, navHostController)
    }
