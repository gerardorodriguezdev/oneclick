package theoneclick.client.core.platform

import theoneclick.client.core.extensions.popUpToInclusive
import theoneclick.client.core.navigation.NavigationController
import theoneclick.client.core.navigation.NavigationController.NavigationEvent
import theoneclick.shared.core.models.routes.AppRoute

interface LogoutManager {
    suspend fun logout()

    suspend fun NavigationController.logout() {
        sendNavigationEvent(
            NavigationEvent.Navigate(
                destinationRoute = AppRoute.Login,
                launchSingleTop = true,
                popUpTo = popUpToInclusive(startRoute = AppRoute.Home)
            )
        )
    }
}