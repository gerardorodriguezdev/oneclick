package theoneclick.client.app.platform

import theoneclick.client.app.extensions.popUpToInclusive
import theoneclick.client.app.navigation.NavigationController
import theoneclick.client.app.navigation.NavigationController.NavigationEvent
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
