package theoneclick.client.shared.network.platform

import theoneclick.client.shared.navigation.NavigationController
import theoneclick.client.shared.navigation.NavigationController.NavigationEvent
import theoneclick.client.shared.navigation.models.routes.AppRoute
import theoneclick.client.shared.navigation.popUpToInclusive

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
