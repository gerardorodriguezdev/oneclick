package theoneclick.client.core.navigation

import theoneclick.client.core.extensions.popUpToInclusive
import theoneclick.shared.core.models.routes.AppRoute

suspend fun NavigationController.logout() {
    sendNavigationEvent(
        NavigationController.NavigationEvent.Navigate(
            destinationRoute = AppRoute.Login,
            launchSingleTop = true,
            popUpTo = popUpToInclusive(startRoute = AppRoute.Init)
        )
    )
}
