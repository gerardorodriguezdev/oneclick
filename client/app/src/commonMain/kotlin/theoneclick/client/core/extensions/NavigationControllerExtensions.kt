package theoneclick.client.core.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import org.koin.compose.getKoin
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import theoneclick.client.core.navigation.NavigationController
import theoneclick.client.core.navigation.NavigationController.NavigationEvent
import theoneclick.client.core.navigation.NavigationControllerObserver
import theoneclick.client.core.navigation.rememberNavigationObserver
import theoneclick.shared.core.models.routes.AppRoute.Home
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

@Composable
fun NavHostController.getOrCreateScope(scopeId: String): Scope {
    val koin = getKoin()
    val backStackEntry = remember { getBackStackEntry(Home) }
    return remember(backStackEntry) {
        koin.getOrCreateScope(
            scopeId = backStackEntry.id,
            qualifier = named(scopeId),
        )
    }
}