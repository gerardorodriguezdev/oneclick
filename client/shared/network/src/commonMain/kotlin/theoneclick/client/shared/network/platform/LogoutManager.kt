package theoneclick.client.shared.network.platform

import theoneclick.client.shared.navigation.NavigationController
import theoneclick.client.shared.navigation.NavigationController.NavigationEvent
import theoneclick.client.shared.navigation.popUpToInclusive
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
